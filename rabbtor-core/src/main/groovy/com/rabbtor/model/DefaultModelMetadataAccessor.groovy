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

            def prop = model.getProperties().find { it.name == part }
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
        return getModelNameCodes(propertyPath,null)
    }

    @Override
    String[] getModelNameCodes(String propertyPath, String modelName)
    {
        Assert.hasText(propertyPath,'propertyPath must not be empty.')

        def result = []
        String propertyName = getPropertyNameFromPath(propertyPath)
        result << propertyName

        ModelPropertyMetadata metadata = getPropertyMetadata(propertyPath);
        ModelMetadata modelMeta = null
        if (metadata != null) {
            if (!modelName) {
                modelMeta = metadata.getDeclaringModelMetadata()
                modelName = modelMeta.modelName
            }
        }

        if (!modelName)
            modelName = createDefaultModelName(modelMeta)
        result << modelName + Errors.NESTED_PATH_SEPARATOR + propertyName

        if (metadata != null) {
            if (StringUtils.hasText(metadata.modelName) && !propertyName.equalsIgnoreCase(metadata.modelName))
            {
                result << metadata.modelName
                result << modelName + Errors.NESTED_PATH_SEPARATOR + propertyName
            }
        }

        result = result.reverse()

        result as String[]
    }

    @Override
    String getDisplayName(String propertyPath)
    {
        String propName = getPropertyNameFromPath(propertyPath)
        ModelPropertyMetadata propertyMetadata = getPropertyMetadata(propertyPath)
        if (propertyMetadata != null && StringUtils.hasText(propertyMetadata.displayName) )
            propName = propertyMetadata.displayName
        return propName
    }

    String createDefaultModelName(ModelMetadata modelMetadata)
    {
        return ClassUtils.getShortNameAsProperty(modelMetadata.modelType)
    }

    String getPropertyNameFromPath(String propertyPath)
    {
        String propName = propertyPath
        if (propName.endsWith('.'))
            propName = propName.substring(0,propName.length()-1)

        def lastDot = propName.lastIndexOf('.')
        if (lastDot != -1)
            propName = propName.substring(0,lastDot)

        return propName
    }
}
