package com.example.processor

import com.example.processor.utils.*
import com.squareup.javapoet.*
import com.sun.tools.javac.code.Type
import javax.lang.model.element.TypeElement

abstract class ClassGenerator(
    val clazz: AnnotatedModelClass
) {

    companion object {
        val bundleClassName = ClassName.get("android.os", "Bundle")
        private val iteratorClassName = ClassName.get("java.util", "Iterator")
        private val collectionClassName = ClassName.get("java.util", "Collection")
        private val arrayListClassName = ClassName.get("java.util", "ArrayList")

    }

    protected val BUNDLE_NAME = "bundle"
    private val CLASS_NAME_SUFFIX = "Bundler"

    protected val classBuilder: TypeSpec.Builder
    protected abstract val getBundleFuncBuilder: MethodSpec.Builder

    init {
        classBuilder =
            TypeSpec.classBuilder("${clazz.getClassName()}$CLASS_NAME_SUFFIX")
    }

    abstract fun generate(): JavaFile

    // this function is called for every class field to generate a put statement based on it's type
    protected fun createPutStatement(field: ModelClassField): CodeBlock {
        val fieldTypeName = TypeName.get(field.element.asType())
        return if (isRawType(fieldTypeName)) {
            createPutRawStatement(field)
        } else if (isBundleable(field.element)) {
            createPutBundleStatement(field)
        } else if (isParcelable(field.element)) {
            createPutParcelableStatement(field)
        } else if (isList(fieldTypeName) || isSet(fieldTypeName)) {
            createPutListStatement(field)
        } else if (isMap(fieldTypeName)) {
            createPutMapStatement(field)
        } else {
            throw java.lang.IllegalArgumentException("Unknown put statement")
        }
    }

    // this function is used to generate a put expression for parcelable data type
    private fun createPutParcelableStatement(field: ModelClassField): CodeBlock {
        val statementBuilder = CodeBlock.builder()

        statementBuilder.addStatement(
            "\$N.put\$L(\$S, \$L)",
            BUNDLE_NAME,
            "Parcelable",
            field.key,
            getValueStatement(field)
        )

        return statementBuilder.build()
    }

    // this function is used to generate a field get expression (getXXX())
    private fun createGetterStatement(field: ModelClassField): String {
        return "get${field.element.simpleName.toString().capitalize()}()"
    }

    // this function is used to generate put expression for primitive + string data type
    private fun createPutRawStatement(field: ModelClassField): CodeBlock {
        val statementBuilder = CodeBlock.builder()

        statementBuilder.addStatement(
            "\$N.put\$L(\$S, \$L)",
            BUNDLE_NAME,
            RAW_BUNDLE_TYPE[field.element.asType().toString()],
            field.key,
            getValueStatement(field)
        )

        return addNullCheck(field, statementBuilder.build())
    }

    // Some data may have an owner so that we need to specify the owner in order to get the data
    // We first check whether the data is owned by an instance
    // If true (not empty) then we need to make a getter expression for that owner instance (owner.getXXX())
    // If false (empty) then we can access that data directly by using it's name
    private fun getValueStatement(field: ModelClassField): String {
        val ownerName = getOwnerName(field)
        return if (ownerName.isEmpty()) {
            field.element.simpleName.toString()
        } else {
            "${ownerName}.${createGetterStatement(field)}"
        }
    }

    private fun getOwnerName(field: ModelClassField): String {
        return if (isOwnedByThis(field)) {
            "${getOwner(field).split(".").last().decapitalize()}"
        } else {
            ""
        }
    }

    private fun isOwnedByThis(field: ModelClassField) = getOwner(field) == clazz.getFqName()

    // this function is used to generate a put expression for a map data type field
    private fun createPutMapStatement(field: ModelClassField): CodeBlock {
        val statementBuilder = CodeBlock.builder()
        val valueType = (field.element.asType() as Type.ClassType).typeArguments[1]
        val valueTypeName = TypeName.get(valueType)

        if (isParcelable(valueType.asElement())) {
            val parameterizedArrayListTypeName = ParameterizedTypeName.get(arrayListClassName, valueTypeName)
            val arrayListName = "${field.element.simpleName}ArrayList"
            statementBuilder.addStatement(
                "\$T \$N = new \$T(\$L.values())",
                parameterizedArrayListTypeName,
                arrayListName,
                parameterizedArrayListTypeName,
                getValueStatement(field)
            )
            statementBuilder.add(createPutParcelableArrayListStatement(field))
        } else {
            statementBuilder.addStatement(
                "\$T<\$T> \$L = \$L.values()",
                collectionClassName,
                valueType,
                "${field.element.simpleName}Values",
                getValueStatement(field)
            )

            statementBuilder
                .addStatement(
                    "\$T<\$T> ${field.element.simpleName}Iterator = \$L.iterator()",
                    iteratorClassName,
                    valueTypeName,
                    "${field.element.simpleName}Values"
                )
            statementBuilder.add(createPutBundlesStatement(field, valueTypeName))
        }

        return addNullCheck(field, statementBuilder.build())
    }

    // this function is used to generate a put expression for data type field that implement list or set interface
    private fun createPutListStatement(field: ModelClassField): CodeBlock {
        val statementBuilder = CodeBlock.builder()
        val parameterType = (field.element.asType() as Type.ClassType).typarams_field[0]
        val parameterTypeName = TypeName.get(parameterType)

        if (isParcelable(parameterType.asElement() as TypeElement)) {
            val parameterizedArrayListTypeName = ParameterizedTypeName.get(arrayListClassName, parameterTypeName)
            val arrayListName = "${field.element.simpleName}ArrayList"

            statementBuilder.addStatement(
                "\$T \$N = new \$T(\$N)",
                parameterizedArrayListTypeName,
                arrayListName,
                parameterizedArrayListTypeName,
                getValueStatement(field)
            )

            statementBuilder.add(createPutParcelableArrayListStatement(field))
        } else {
            statementBuilder.add(createIteratorStatement(field, parameterTypeName))
            statementBuilder.add(createPutBundlesStatement(field, parameterTypeName))

        }

        return statementBuilder.build()
    }

    // this function is used to generate a put expression for an arraylist of parcelable
    private fun createPutParcelableArrayListStatement(
        field: ModelClassField
    ): CodeBlock {
        val statementBuilder = CodeBlock.builder()
        val arrayListName = "${field.element.simpleName}ArrayList"

        statementBuilder.addStatement(
            "\$N.putParcelableArrayList(\$S, \$N)",
            BUNDLE_NAME,
            field.key,
            arrayListName
        )

        return statementBuilder.build()
    }

    // this function is used to generate an expression to get the iterator of a collection data type
    // Ex: Iterator<Type> typeIterator = type.iterator();
    private fun createIteratorStatement(
        field: ModelClassField,
        parameterTypeName: TypeName
    ): CodeBlock {
        val statementBuilder = CodeBlock.builder()

        statementBuilder
            .addStatement(
                "\$T<\$T> ${field.element.simpleName}Iterator = \$L.iterator()",
                iteratorClassName,
                parameterTypeName,
                getValueStatement(field)
            )

        return statementBuilder.build()
    }

    // this function is used to generate a put expression for an array list of bundle
    private fun createPutBundlesStatement(
        field: ModelClassField,
        parameterTypeName: TypeName
    ): CodeBlock {
        val statementBuilder = CodeBlock.builder()

        statementBuilder.addStatement(
            "\$T<\$T> ${field.element.simpleName}Bundles = new \$T()",
            arrayListClassName,
            bundleClassName,
            arrayListClassName
        )

        statementBuilder.beginControlFlow("while(${field.element.simpleName}Iterator.hasNext()) ")

        statementBuilder.addStatement(
            "\$T current = ${field.element.simpleName}Iterator.next()",
            parameterTypeName
        )

        statementBuilder.add(createPutBundleStatement(field, parameterTypeName))

        statementBuilder.endControlFlow()

        statementBuilder.addStatement(
            "bundle.putParcelableArrayList(\$S, \$L)",
            field.key,
            "${field.element.simpleName}Bundles"
        )

        return statementBuilder.build()
    }

    // this function is used to generate a put exression for Bundle data type
    // or to generate an add expression
    // Ex: bundle.putBundle("key", Bundler.getBundle(data)) or someArrayListOfBundle.add(Bundler.getBundle(data))
    private fun createPutBundleStatement(
        field: ModelClassField,
        parameterTypeName: TypeName? = null
    ): CodeBlock {
        val statementBuilder = CodeBlock.builder()
        val fieldBundlerClass = getBundlerClassName(parameterTypeName, field)

        if (parameterTypeName == null) {
            statementBuilder.addStatement(
                "bundle.putBundle(\$S, \$T.getBundle(\$L))",
                field.key,
                fieldBundlerClass,
                getValueStatement(field)
            )
        } else {
            statementBuilder.addStatement(
                "${field.element.simpleName}Bundles.add(\$T.getBundle(current))",
                fieldBundlerClass
            )
        }

        return statementBuilder.build()
    }

    // this function is to get the generated bundler class name
    private fun getBundlerClassName(typeName: TypeName?, field: ModelClassField): ClassName {
        val fieldTypeFqName = typeName?.toString()?.split(".")
            ?: TypeName.get(field.element.asType()).toString().split(".")

        val fieldPackage = fieldTypeFqName.take(fieldTypeFqName.size - 1).joinToString(".")
        val fieldClassName = fieldTypeFqName.last()

        return ClassName.get(fieldPackage, "${fieldClassName}Bundler")
    }

    private fun addNullCheck(field: ModelClassField, codeBlock: CodeBlock): CodeBlock {
        val statementBuilder = CodeBlock.builder()

        if (field.isNullable) {

            statementBuilder
                .beginControlFlow(
                    "if(\$L == null)",
                    getValueStatement(field)
                )

            val defaultValueWithCast = createDefaultValueCaster(field)

            statementBuilder
                .addStatement(
                    "\$N.put\$L(\$S, \$L)",
                    BUNDLE_NAME,
                    RAW_BUNDLE_TYPE[field.element.asType().toString()],
                    field.element.simpleName,
                    defaultValueWithCast
                )
                .nextControlFlow("else")
        }

        statementBuilder.add(codeBlock)

        if (field.isNullable) {
            statementBuilder.endControlFlow()
        }

        return statementBuilder.build()
    }

    // this function is used to generate a cast expression for some data type that must be casted
    private fun createDefaultValueCaster(field: ModelClassField): String {
        val caster = when (field.element.asType().toString().split(".").last()) {
            "Byte" -> "(byte)"
            "Short" -> "(short)"
            "Int" -> "(int)"
            "Long" -> "(long)"
            "Float" -> "(float)"
            else -> ""
        }

        return "$caster ${field.defaultValue}"
    }

}