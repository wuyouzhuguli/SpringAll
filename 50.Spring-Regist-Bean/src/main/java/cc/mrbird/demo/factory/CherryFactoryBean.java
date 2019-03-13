package cc.mrbird.demo.factory;

import cc.mrbird.demo.domain.Cherry;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author MrBird
 */
public class CherryFactoryBean implements FactoryBean<Cherry> {
    @Override
    public Cherry getObject() {
        return new Cherry();
    }

    @Override
    public Class<?> getObjectType() {
        return Cherry.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
