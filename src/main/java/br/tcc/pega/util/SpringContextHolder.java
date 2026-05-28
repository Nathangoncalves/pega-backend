package br.tcc.pega.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Permite que classes não-gerenciadas pelo Spring (como agentes JADE)
 * obtenham beans Spring em tempo de execução.
 *
 * Padrão necessário porque agentes JADE são criados via reflexão pelo container
 * JADE, fora do ciclo de injeção de dependência do Spring.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SpringContextHolder.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
        log.info("SpringContextHolder inicializado — beans disponíveis para agentes JADE.");
    }

    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}
