package Autowired.Tools.Processor;

public interface BeanPostProcessor
{
   default Object postProcessBeforeInitialization(Object bean, String beanName)
   {
       return bean;
   }

    default Object  postProcessAfterInitialization(Object bean, String beanName)
    {
        return bean;
    }
}
