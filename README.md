#   Manipulating Entity ID before it gets inserted
##  Introduction
In this tutorial I will explain how to manipulate an entity ``ID`` even before it gets inserted into the database table.

For that purpose I will use spring framework (spring -boot) and hibernate to do that.
##  Use case
Assume that you have a column that you need to update based on the ``ID `` of your row; however the entity is not created yet; so the ``ID`` doesn't exist yet.

In our case let's use an entity we called Odd. This entity contains only two properties, the ID and another property to store whether the ID is odd or even.
##  GitHub repository
A working spring-boot project is available on this [GitHub repository](https://github.com/Ibrahimhammani/id-before-insert/)
##  Let's get hands dirty
### Use a sequence generator for the ID
The first step is delegating the  ``ID`` generation to Hibernate by using a sequence generation strategy and defining a sequence generator,that way it will be generated and included in the insert query; that hibernate will send to the RDBMS for persistence.

Be careful to not use a database sequence; because in that case the RDBMS will generate the id on its own.

```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
@SequenceGenerator(name = "sequenceGenerator")
@Column(name = "id")
private Long id;
```
### Use a PreInsertEventListener
Luckily hibernate allows us to define a callback just before inserting a row into the database; and since the id is already generated, we will be able to use it in the event listener.

```java
@Component
public class PreInsertListener implements PreInsertEventListener {

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Odd entityCasted = (Odd) event.getEntity();
        if (entityCasted.getId() == null) {
            throw new UnknownId("Entity id is null");
        }
        Boolean isOdd = (entityCasted.getId() % 2 == 0);
        /*
            update the state of the entity
            this is enough for saving the right values in the database
        */
        event.getState()[ArrayUtils.indexOf(
                event.getPersister().getEntityMetamodel().getPropertyNames(),
                "isOdd")] = isOdd;

        /*
            Even if the right state is saved in the database
            hibernate will not update the entity field, we need to do this by ourselves
        */
        entityCasted.setIsOdd(isOdd);
        return false;
    }
}
```

### Add the EventListenerIntegrator
To be able to use the ``PreInsertListener`` we just wrote, we need to ask hibernate to register it; so it will be aware of its existence.

```java
@Component
@RequiredArgsConstructor
public class EventListenerIntegrator implements Integrator {

    private final PreInsertListener preInsertListener;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        /*
            Register the event listener
         */
        eventListenerRegistry.appendListeners(EventType.PRE_INSERT, preInsertListener);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}
```

### Add the integrator to hibernate properties
The final step is adding this integrator to hibernate properties, so he will be available for use.

```java
@Component
@RequiredArgsConstructor
public class HibernateConfig implements HibernatePropertiesCustomizer {

    private final EventListenerIntegrator eventListenerIntegrator;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                "hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(eventListenerIntegrator)
        );
    }
}
```

##  It's showtime
### Logging the SQL queries
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
### Logging SQL queries parameters
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type=trace
```
### Running a test:
To tests the whole thing lets tun this basic tests

```java
@Test
public void saveTest(){
        Odd savedOdd1 = oddRepository.save(new Odd());
        assertFalse(savedOdd1.getIsOdd());
        Odd savedOdd2 = oddRepository.save(new Odd());
        assertTrue(savedOdd2.getIsOdd());
        }
```

### Logs

If we look close to the logs we can see that hibernate generates IDs first then inserts the entity row into the database, the values inserted in the database are also logged.
```log
2022-10-31 22:15:01.927 DEBUG 2079 --- [           main] org.hibernate.SQL                        : 
    call next value for sequence_generator
2022-10-31 22:15:01.953 DEBUG 2079 --- [           main] org.hibernate.SQL                        : 
    insert 
    into
        odd
        (is_odd, id) 
    values
        (?, ?)
2022-10-31 22:15:01.956 TRACE 2079 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BOOLEAN] - [false]
2022-10-31 22:15:01.957 TRACE 2079 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [BIGINT] - [1]
2022-10-31 22:15:01.963 DEBUG 2079 --- [           main] org.hibernate.SQL                        : 
    call next value for sequence_generator
2022-10-31 22:15:01.963 DEBUG 2079 --- [           main] org.hibernate.SQL                        : 
    insert 
    into
        odd
        (is_odd, id) 
    values
        (?, ?)
2022-10-31 22:15:01.964 TRACE 2079 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BOOLEAN] - [true]
2022-10-31 22:15:01.964 TRACE 2079 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [BIGINT] - [2]
```

