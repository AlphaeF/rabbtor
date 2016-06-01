package com.rabbtor.model

import groovy.transform.CompileStatic;
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils
import org.springframework.validation.Errors;

@CompileStatic
public class DefaultModelMetadataAccessor implements ModelMetadataAccessor
{
    private ModelMetadataRegistry metadataRegistry;
    private Class modelType;
    private String modelName;


    public DefaultModelMetadataAccessor(Class modelType)
    {
        Assert.notNull(modelType,"modelType must not be null.");
        this.modelType = modelType;
        metadataRegistry = new DefaultModelMetadataRegistry()
    }

    public ModelMetadataRegistry getMetadataRegistry()
    {
        return metadataRegistry;
    }

    public void setMetadataRegistry(ModelMetadataRegistry provider)
    {
        Assert.notNull(provider,"modelMetadataRegistry must not be null.");
        this.metadataRegistry = provider;
    }

    @Override
    public ModelMetadata getModelMetadata()
    {
        return metadataRegistry.getModelMetadata(modelType);
    }

    @Override
    public ModelPropertyMetadata getPropertyMetadata(String propertyPath)
    {
        def parts = propertyPath.split("\\.").toList();
        ModelMetadata model = getModelMetadata();
        if (model == null)
            return null;

        while (parts.any()) {
            def part = parts.first()
            parts.remove(0)

            boolean hasIndex = false
            if (part.contains('['))
            {
                part = part.substring(0, part.indexOf('['))
                hasIndex = true
            }

            def prop = model.getProperties().find { it.propertyName == part }
            if (prop == null)
                return null

            if (!hasIndex && !parts.any())
                return prop

            if (hasIndex)
                model = prop.getComponentMetadata()
            else
                model = prop.getPropertyTypeMetadata()

            if (!model)
                return null

        }

        return null;
    }

    @Override
    String[] getModelNameCodes(String propertyPath)
    {
        return getModelNameCodes(propertyPath,getModelName())
    }

    @Override
    String[] getModelNameCodes(String propertyPath, String modelName)
    {
        Assert.hasText(propertyPath,'propertyPath must not be empty.')

        def result = []
        String propertyName = getPropertyNameFromPath(propertyPath)
        result << propertyName


        String declaringModelName;
        if (propertyPath.contains(Errors.NESTED_PATH_SEPARATOR)) {
            // nested property. find declaring metadata
            ModelPropertyMetadata metadata = getPropertyMetadata(propertyPath)
            ModelMetadata declaringModelMetadata = metadata.getDeclaringModelMetadata()
            if (declaringModelMetadata != null) {
                declaringModelName = declaringModelMetadata.modelName
                if (declaringModelName == null)
                    declaringModelName = createDefaultModelName(declaringModelMetadata)
            }

            if (StringUtils.hasText(declaringModelName))
                result << declaringModelName + Errors.NESTED_PATH_SEPARATOR + propertyName
        }

        if (modelName == null)
            modelName = createDefaultModelName(modelMetadata)

        String propertyPathWithoutIndexes = propertyPath.replaceAll("\\[.*\\]","")
        if (propertyPathWithoutIndexes.length() != propertyPath)
            result << modelName + Errors.NESTED_PATH_SEPARATOR + propertyPathWithoutIndexes

        if (StringUtils.hasText(modelName))
            result << modelName + Errors.NESTED_PATH_SEPARATOR + propertyPath

        result = result.reverse()

        result as String[]
    }

    @Override
    String getDisplayName(String propertyPath)
    {
        Assert.hasText(propertyPath,"propertyPath must not be empty.")
        ModelPropertyMetadata propertyMetadata = getPropertyMetadata(propertyPath)
        if (propertyMetadata == null)
            return getPropertyNameFromPath(propertyPath);

        if (StringUtils.hasText(propertyMetadata.displayName))
            return propertyMetadata.displayName
        return getPropertyNameFromPath(propertyPath)
    }

    @Override
    String getModelName()
    {
        if (modelName != null)
            return modelName

        return getModelNameOrDefault(modelMetadata)
    }

    protected String getModelNameOrDefault(ModelMetadata modelMetadata)
    {
        if (modelMetadata != null && modelMetadata.modelName != null)
            return modelMetadata.modelName
        return createDefaultModelName(modelMetadata)
    }

    @Override
    void setModelName(String modelName)
    {
        Assert.notNull(modelName,"modelName must not be null.For empty model names, please use empty string(\"\").")
        this.modelName = modelName;
    }

    String createDefaultModelName(ModelMetadata modelMetadata)
    {
        return ClassUtils.getShortNameAsProperty(modelMetadata.modelType)
    }

    String getPropertyNameFromPath(String propertyPath)
    {
        String propName = propertyPath
        if (propName.endsWith(Errors.NESTED_PATH_SEPARATOR))
            propName = propName.substring(0,propName.length()-Errors.NESTED_PATH_SEPARATOR.length())

        def lastDot = propName.lastIndexOf('.')
        if (lastDot != -1)
            propName = propName.substring(lastDot+1,propName.length())

        return propName
    }
}
