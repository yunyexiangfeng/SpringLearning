# Hibernate user guide

## 1. Persistence Context





## 2. Persistence Context



## 3. Bootstrap

引导程序是指初始化和启动软件组件。在Hibernate中，我们专门讨论为JPA构建功能完整的SessionFactory实例或EntityManagerFactory实例的过程

###3.1. Native Bootstrapping

本地引导的==第一步==是建立一个ServiceRegistry。Hibernate在引导过程中和运行时所需的服务。

####3.1.1. Building the ServiceRegistry

第一个ServiceRegistry是org.hibernate.boot.registry.BootstrapServiceRegistry。

BootstrapServiceRegistry是为了保持服务Hibernate需要在两个引导和运行时间。这归结为3种服务:

1. org.hibernate.boot.registry.classloading.spi.ClassLoaderService

   它控制Hibernate如何与ClassLoaders交互

2. org.hibernate.integrator.spi.IntegratorService

   它控制org.hibernate.integrator.spi.Integrator实例的管理和发现。

3. org.hibernate.boot.registry.selector.spi.StrategySelector

   它控制了Hibernate如何解决各种策略联合的实现。这是一项功能非常强大的服务，但是对其的完整讨论超出了本指南的范围。

```tex
注：
如果所使用的Hibernate的BootstrapServiceRegistry服务的默认构建行为已经能满足要求（这是常见的情况，尤其是在独立环境中），则无需显式构建BootstrapServiceRegistry。
```

如果您希望更改的BootstrapServiceRegistry构建方式，则可以通过进行控制org.hibernate.boot.registry.BootstrapServiceRegistryBuilder：

```java
@Override
protected void setUp() throws Exception {
    BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
    // add a custom ClassLoader
    builder.applyClassLoader(customClassLoader);
    // manually add an Integrator
    builder.applyIntegrator(customIntegrator);
    BootstrapServiceRegistry registry = builder.build();
}
```

```tex
注：
BootstrapServiceRegistry的服务不能被扩展，也不能被重写。
```



第二个ServiceRegistry是org.hibernate.boot.registry.StandardServiceRegistry。

您几乎总是需要配置StandardServiceRegistry，这可以通过org.hibernate.boot.registry.StandardServiceRegistryBuilder以下步骤完成：

```java
/**
     * Example 268. Configuring a MetadataSources
     */
private void example(){

    ServiceRegistry registry = new StandardServiceRegistryBuilder().build();

    MetadataSources sources = new MetadataSources(registry);
    // alternatively, we can build the MetadataSources without passing
    // a service registry, in which case it will build a default
    // BootstrapServiceRegistry to use.  But the approach shown
    // above is preferred
    // MetadataSources sources = new MetadataSources();

    // add a class using JPA/Hibernate annotations for mapping
    sources.addAnnotatedClass( MyEntity.class );

    // add the name of a class using JPA/Hibernate annotations for mapping.
    // differs from above in that accessing the Class is deferred which is
    // important if using runtime bytecode-enhancement
    sources.addAnnotatedClassName("com.oct.chap3.entity.Customer");

    // Read package-level metadata.
    sources.addPackage("com.oct");

    // Read package-level metadata.
    sources.addPackage(MyEntity.class.getPackage());

    // Adds the named hbm.xml resource as a source: which performs the
    // classpath lookup and parses the XML
    sources.addResource("chap3/Order.hbm.xml");

    // Adds the named JPA orm.xml resource as a source: which performs the
    // classpath lookup and parses the XML
    sources.addResource( "chap3/Product.orm.xml" );

    // Read all mapping documents from a directory tree.
    // Assumes that any file named *.hbm.xml is a mapping document.
    sources.addDirectory(new File("."));

    // Read mappings from a particular XML file
    sources.addJar(new File("./entities.jar"));

    // Read a mapping as an application resource using the convention that a class named foo.bar.MyEntity is
    // mapped by a file named foo/bar/MyEntity.hbm.xml which can be resolved as a classpath resource.
    sources.addClass(MyEntity.class);
}
```



####3.1.2. Event Listener registration

现在，主要的用例org.hibernate.integrator.spi.Integrator是注册事件侦听器并提供服务（请参阅参考资料org.hibernate.integrator.spi.ServiceContributingIntegrator）。在5.0版本中，我们计划对其进行扩展，以允许更改描述对象模型和关系模型之间映射的元模型。



#### 3.1.3. Building the Metadata

本地引导的==第二步==是构建一个org.hibernate.boot.Metadata对象，该对象包含应用程序域模型的已解析表示形式及其到数据库的映射。

显然我们需要做的第一件事是构建一个解析表示形式的第一件事是要解析的源信息（带注释的类，`hbm.xml`文件，`orm.xml`文件）。

MetadataSources还有许多其他方法。探索其API和Javadocs以获得更多信息。

*Example 270. Configuring a* `MetadataSources` *with method chaining*

```java
ServiceRegistry standardRegistry =
    new StandardServiceRegistryBuilder().build();

MetadataSources sources = new MetadataSources( standardRegistry )
    .addAnnotatedClass( MyEntity.class )
    .addAnnotatedClassName( "com.oct.chap3.entity.Customer" )
    .addResource( "chap3/Order.hbm.xml" )
    .addResource( "chap3/Product.orm.xml" );
Metadata metadata = sources.buildMetadata();
```

一旦定义了映射信息的来源，就需要构建Metadata对象。如果您可以接受建立元数据的默认行为，则只需调用的buildMetadata方法MetadataSources。

```tex
注：
略
```

但是，如果你要调整建设过程中Metadata从MetadataSources，你就需要使用MetadataBuilder如通过获得MetadataSources#getMetadataBuilder。 MetadataBuilder允许对Metadata构建过程进行大量控制。

*Example 271. Building Metadata via* `MetadataBuilder`

```java
ServiceRegistry registry = new StandardServiceRegistryBuilder().build();

MetadataSources metadataSources = new MetadataSources(registry);

MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

// Use the JPA-compliant implicit naming strategy
metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);

// specify the schema name to use for tables, etc when none is explicitly specified
metadataBuilder.applyImplicitSchemaName( "my_default_schema" );

// specify a custom Attribute Converter
//        metadataBuilder.applyAttributeConverter( myAttributeConverter );

Metadata metadata = metadataBuilder.build();
```



#### 3.1.4. Building the SessionFactory

本地引导的==最后一步==是构建SessionFactory自身。

就像上面讨论的一样，如果您认为从Metadata引用构建一个SessionFactory的默认行为没问题，则可以简单地buildSessionFactory在Metadata对象上调用方法。

但是，如果您要调整该构建过程，则需要使用SessionFactoryBuilder通过获取的Metadata#getSessionFactoryBuilder。

*Example 272. Native Bootstrapping - Putting it all together*

```java
StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
    .configure( "org/hibernate/example/hibernate.cfg.xml" )
    .build();

Metadata metadata = new MetadataSources( standardRegistry )
    .addAnnotatedClass( MyEntity.class )
    .addAnnotatedClassName( "org.hibernate.example.Customer" )
    .addResource( "org/hibernate/example/Order.hbm.xml" )
    .addResource( "org/hibernate/example/Product.orm.xml" )
    .getMetadataBuilder()
    .applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
    .build();

SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
    .applyBeanManager( getBeanManager() )
    .build();
```

 bootstrapping API非常灵活，但是在大多数情况下，将中3个步骤的过程是最有意义的：

1. 建立 StandardServiceRegistry

2. 建立 Metadata

3. 使用那两个来建立 SessionFactory

*Example 273. Building* `SessionFactory` *via* `SessionFactoryBuilder`

```java
StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
        .configure( "org/hibernate/example/hibernate.cfg.xml" )
        .build();

Metadata metadata = new MetadataSources( standardRegistry )
    .addAnnotatedClass( MyEntity.class )
    .addAnnotatedClassName( "org.hibernate.example.Customer" )
    .addResource( "org/hibernate/example/Order.hbm.xml" )
    .addResource( "org/hibernate/example/Product.orm.xml" )
    .getMetadataBuilder()
    .applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
    .build();

SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();

// Supply a SessionFactory-level Interceptor
sessionFactoryBuilder.applyInterceptor( new CustomSessionFactoryInterceptor() );

// Add a custom observer
sessionFactoryBuilder.addSessionFactoryObservers( new CustomSessionFactoryObserver() );

// Apply a CDI BeanManager ( for JPA event listeners )
sessionFactoryBuilder.applyBeanManager( getBeanManager() );

SessionFactory sessionFactory = sessionFactoryBuilder.build();
```



### 3.2. JPA Bootstrapping

将Hibernate作为JPA提供程序进行引导可以通过符合JPA规范的方式或使用专有的引导方法来完成。标准化方法在某些环境中有一些限制，但除了这些限制外，强烈建议您使用标准化的JPA引导程序。

#### 3.2.1. JPA-compliant bootstrapping

JPA规范定义了两种主要的标准化引导程序方法，具体取决于应用程序打算如何从EntityManagerFactory中访问javax.persistence.EntityManager实例。

对于兼容的==容器引导==（J2EE），容器将为配置文件中EntityManagerFactory定义的每个持久性单元构建一个，META-INF/persistence.xml并通过javax.persistence.PersistenceUnit注释或JNDI查找将其提供给应用程序以进行注入。

*Example 274. Injecting the default* `EntityManagerFactory`

```java
@PersistenceUnit
private EntityManagerFactory emf;
```

或者，如果您有多个持久性单元（例如，多个`persistence.xml`配置文件），则可以`EntityManagerFactory`按单元名称注入特定的：

*Example 275. Injecting a specific* `EntityManagerFactory`

```java
@PersistenceUnit(
    unitName = "CRM"
)
private EntityManagerFactory entityManagerFactory;
```

该META-INF/persistence.xml文件如下所示：

*Example 276. META-INF/persistence.xml configuration file*

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">

    <persistence-unit name="CRM">
        <description>
            Persistence unit for Hibernate User Guide
        </description>

        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>org.hibernate.documentation.userguide.Document</class>

        <properties>
            <property name="javax.persistence.jdbc.driver"
                      value="org.h2.Driver" />

            <property name="javax.persistence.jdbc.url"
                      value="jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE" />

            <property name="javax.persistence.jdbc.user"
                      value="sa" />

            <property name="javax.persistence.jdbc.password"
                      value="" />

            <property name="hibernate.show_sql"
                      value="true" />

            <property name="hibernate.hbm2ddl.auto"
                      value="update" />
        </properties>

    </persistence-unit>

</persistence>
```

对于兼容的==应用程序引导==（J2SE），应用程序使用javax.persistence.Persistence bootstrap类构建EntityManagerFactory ，而不是使用容器为应用程序构建EntityManagerFactory（注：即使用@PersistenceUnit的方式）。应用程序通过调用createEntityManagerFactory方法构建一个EntityManagerFactory：

*Example 277. Application bootstrapped* `EntityManagerFactory`

```java
// Create an EMF for our CRM persistence-unit.
EntityManagerFactory emf = Persistence.createEntityManagerFactory( "CRM" );
```

```tex
注：
如果您不想提供persistence.xml配置文件，则JPA允许你在一个PersistenceUnitInfo的实现中提供所有的配置，并调用HibernatePersistenceProvider.html#createContainerEntityManagerFactory。
```

要注入默认的持久性上下文，可以使用@PersistenceContext注释。

*Example 278. Inject the default* `EntityManager`

```java
@PersistenceContext
private EntityManager em;
```

要注入特定的持久性上下文，可以使用@PersistenceContext注释，甚至可以使用注释传递EntityManager特定于属性的属性 @PersistenceProperty。

*Example 279. Inject a configurable* `EntityManager`

```java
@PersistenceContext(
    unitName = "CRM",
    properties = {
        @PersistenceProperty(
            name="org.hibernate.flushMode",
            value= "MANUAL"
        )
    }
)
private EntityManager entityManager;
```



#### 3.2.2. Externalizing XML mapping files

JPA提供了两个映射选项：

- 注解

- XML映射

尽管注释更为常见，但在某些项目中首选XML映射。您甚至可以混合使用注释和XML映射，以便可以使用XML配置覆盖注释映射，这些配置可以轻松更改而无需重新编译项目源代码。这是可能的，因为如果存在两个冲突的映射，则XML映射优先于其注释副本。

JPA规范要求XML映射位于类路径上：

```tex
orm.xml可以META-INF在持久性单元根目录中的目录中，或在所META-INF引用的任何jar文件的目录中指定名为的对象/关系映射XML文件persistence.xml。

或者 or 另外，持久性单元元素的映射文件元素可以引用一个或多个映射文件。这些映射文件可能存在于类路径上的任何位置。

															— JPA 2.1规范的第8.2.1.6.2节
```

因此，映射文件可以驻留在应用程序jar构件中，或者可以将它们存储在外部文件夹位置，并且可以考虑将该位置包含在类路径中。

Hibernate在这方面更为宽大，因此您甚至可以在应用程序配置的类路径之外使用任何外部位置。

*Example 280. META-INF/persistence.xml configuration file for external XML mappings*

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">

    <persistence-unit name="CRM">
        <description>
            Persistence unit for Hibernate User Guide
        </description>

        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <mapping-file>file:///etc/opt/app/mappings/orm.xml</mapping-file>

        <properties>
            <property name="javax.persistence.jdbc.driver"
                      value="org.h2.Driver" />

            <property name="javax.persistence.jdbc.url"
                      value="jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE" />

            <property name="javax.persistence.jdbc.user"
                      value="sa" />

            <property name="javax.persistence.jdbc.password"
                      value="" />

            <property name="hibernate.show_sql"
                      value="true" />

            <property name="hibernate.hbm2ddl.auto"
                      value="update" />
        </properties>

    </persistence-unit>

</persistence>
```

在persistence.xml上面的配置文件中，orm.xml包含所有JPA实体映射的XML文件位于该/etc/opt/app/mappings/文件夹中。

####3.2.3. Configuring the SessionFactory Metadata via the JPA bootstrap



## 4. Schema generation

Hibernate允许您从实体映射生成数据库。

传统上，从实体映射生成架构的过程称为HBM2DDL。要获取Hibernate本地和JPA特定配置属性的列表，请考虑阅读“配置“部分。

```tex
配置链接：
https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#configurations-hbmddl
```

考虑以下域模型：

```java
@Entity(name = "Customer")
public class Customer {

	@Id
	private Integer id;

	private String name;

	@Basic( fetch = FetchType.LAZY )
	private UUID accountsPayableXrefId;

	@Lob
	@Basic( fetch = FetchType.LAZY )
	@LazyGroup( "lobs" )
	private Blob image;

	//Getters and setters are omitted for brevity

}

@Entity(name = "Person")
public static class Person {

	@Id
	private Long id;

	private String name;

	@OneToMany(mappedBy = "author")
	private List<Book> books = new ArrayList<>();

	//Getters and setters are omitted for brevity

}

@Entity(name = "Book")
public static class Book {

	@Id
	private Long id;

	private String title;

	@NaturalId
	private String isbn;

	@ManyToOne
	private Person author;

	//Getters and setters are omitted for brevity

}
```

如果hibernate.hbm2ddl.auto配置设置为create，则Hibernate将生成以下数据库架构：

*Example 283. Auto-generated database schema*

```sql
create table Customer (
    id integer not null,
    accountsPayableXrefId binary,
    image blob,
    name varchar(255),
    primary key (id)
)

create table Book (
    id bigint not null,
    isbn varchar(255),
    title varchar(255),
    author_id bigint,
    primary key (id)
)

create table Person (
    id bigint not null,
    name varchar(255),
    primary key (id)
)

alter table Book
    add constraint UK_u31e1frmjp9mxf8k8tmp990i unique (isbn)

alter table Book
    add constraint FKrxrgiajod1le3gii8whx2doie
    foreign key (author_id)
    references Person
```



### 4.1. Importing script files

To customize the schema generation process, the hibernate.hbm2ddl.import_files configuration property must be used to provide other scripts files that Hibernate can use when the SessionFactory is started.

要自定义schema 生成过程，Hibernate的==SessionFactory启动==时hibernate.hbm2ddl.import_files的值必须配置为指定脚本文件。

*Example 284. Schema generation import file*

```sql
update teacher set t_name='张三' where t_id='01';
```

如果我们将Hibernate配置(hibernate.cfg.xml)为导入上面的脚本：

```xml
<property name="hibernate.hbm2ddl.import_files">schema-generation.sql</property>
```

模式自动生成后，Hibernate将执行脚本文件。测试生成代码：

```java
ServiceRegistry registry = new StandardServiceRegistryBuilder().
    configure("hibernate.cfg.xml").//默认使用hibernate.properties ，所以hibernate.cfg.xml要显示配置
    build();
Metadata metadata = new MetadataSources(registry).buildMetadata();
metadata.buildSessionFactory();
```



### 4.2. Database objects

Hibernate允许您通过HBMdatabase-object元素来自定义schema生成过程。

考虑以下HBM映射：

*Example 286. Schema generation HBM database-object*

```xml
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >

<hibernate-mapping>
    <database-object>
        <create>
            CREATE OR REPLACE FUNCTION sp_count_books(
                IN authorId bigint,
                OUT bookCount bigint)
                RETURNS bigint AS
            $BODY$
                BEGIN
                    SELECT COUNT(*) INTO bookCount
                    FROM book
                    WHERE author_id = authorId;
                END;
            $BODY$
            LANGUAGE plpgsql;
        </create>
        <drop></drop>
        <dialect-scope name="org.hibernate.dialect.PostgreSQL95Dialect" />
    </database-object>
</hibernate-mapping>
```

当SessionFactory引导时，Hibernate将执行database-object，因此创建了sp_count_books函数。



### 4.3. Database-level checks

Hibernate提供了@Check注释，以便您可以指定一个任意的SQL CHECK约束，该约束可以如下定义：

*例子287.数据库检查实体映射例子*

```java
@Entity(name = "Book")
@Check( constraints = "CASE WHEN isbn IS NOT NULL THEN LENGTH(isbn) = 13 ELSE true END")
public static class Book {

	@Id
	private Long id;

	private String title;

	@NaturalId
	private String isbn;

	private Double price;

	//Getters and setters omitted for brevity

}
```

现在，如果您尝试添加Book的isbn属性的长度不是13个字符，将抛出ConstraintViolationException异常。

*Example 288. Database check failure example*

```java
Book book = new Book();
book.setId( 1L );
book.setPrice( 49.99d );
book.setTitle( "High-Performance Java Persistence" );
book.setIsbn( "11-11-2016" );

entityManager.persist( book );
```

```sql
INSERT  INTO Book (isbn, price, title, id)
VALUES  ('11-11-2016', 49.99, 'High-Performance Java Persistence', 1)

-- WARN SqlExceptionHelper:129 - SQL Error: 0, SQLState: 23514
-- ERROR SqlExceptionHelper:131 - ERROR: new row for relation "book" violates check constraint "book_isbn_check"
```





### 4.4. Default value for a database column

使用Hibernate，您可以使用@ColumnDefault注释为给定的数据库列指定默认值。

*Example 289.* `@ColumnDefault` *mapping example*

```java
@Entity(name = "Person")
@DynamicInsert
public static class Person {

    @Id
    private Long id;

    @ColumnDefault("'N/A'")
    private String name;

    @ColumnDefault("-1")
    private Long clientId;

    //Getter and setters omitted for brevity

}
```

```sql
CREATE TABLE Person (
  id BIGINT NOT NULL,
  clientId BIGINT DEFAULT -1,
  name VARCHAR(255) DEFAULT 'N/A',
  PRIMARY KEY (id)
)
```

在上面的映射中，name将使用DEFAULT值。这样，当省略name属性时，数据库将根据其默认值进行设置。





### 4.5. Columns unique constraint



###4.6. Columns index





## 5. Persistence Context

持久化上下文

这两个org.hibernate.SessionAPI及javax.persistence.EntityManagerAPI代表了处理持久性数据的上下文。这个概念称为persistence context

```tex
transient
该实体刚刚被实例化，并且未与持久性上下文关联。它在数据库中没有持久性表示形式，并且通常没有分配标识符值（除非使用了分配的生成器）。

managed or persistent
该实体具有关联的标识符，并且与持久性上下文关联。它实际上可能存在或可能不存在于数据库中。

detached
实体具有关联的标识符，但不再与持久性上下文关联（通常是因为持久性上下文已关闭或实例已从上下文中退出）

removed
该实体具有关联的标识符，并且与持久性上下文关联，但是已安排将其从数据库中删除。
```



###5.1 Accessing Hibernate APIs from JPA

使用JPA访问Hibernate API

```java
Session session = entityManager.unwrap( Session.class );
SessionImplementor sessionImplementor = entityManager.unwrap( SessionImplementor.class );

SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap( SessionFactory.class );
```



###5.2 Bytecode Enhancement

Hibernate支持增强应用程序Java域模型，以便将各种与持久性相关的功能直接添加到类中。

##### Lazy attribute loading

可以将其视为部分加载支持。从本质上讲，您可以告诉Hibernate从数据库中获取实体时，仅应加载实体的一部分，以及何时应加载其他部分。请注意，这与基于代理的延迟加载思想非常不同，后者以实体为中心，在这种情况下，根据需要立即加载实体的状态。通过字节码增强，可以根据需要加载单个属性或属性组。

可以将惰性属性指定为一起加载，这称为“惰性组”。默认情况下，所有单数属性都是单个组的一部分，这意味着当访问一个惰性单数属性时，将加载所有惰性单数属性。默认情况下，惰性复数属性本身都是惰性组。此行为可通过@org.hibernate.annotations.LazyGroup注释显式控制。

*Example 295.* `@LazyGroup` *example*

```java
@Entity
public class Customer {

	@Id
	private Integer id;

	private String name;

	@Basic( fetch = FetchType.LAZY )
	private UUID accountsPayableXrefId;

	@Lob
	@Basic( fetch = FetchType.LAZY )
	@LazyGroup( "lobs" )
	private Blob image;

	//Getters and setters are omitted for brevity

}
```

在上面的示例中，我们有2个惰性属性：accountsPayableXrefId和image。每个属性都是不同的访存组的一部分（accountsPayableXrefId是默认访存组的一部分），这意味着访问accountsPayableXrefId将不会强制加载image属性，反之亦然。

##### In-line dirty tracking

略

##### Bidirectional association management

Hibernate努力使您的应用程序尽可能接近“正常Java使用率”（惯用Java）。考虑具有正常Person/Book双向关联的域模型：

*Example 296. Bidirectional association*

```java
@Entity(name = "Person")
public static class Person {

	@Id
	private Long id;

	private String name;

	@OneToMany(mappedBy = "author")
	private List<Book> books = new ArrayList<>();

	//Getters and setters are omitted for brevity

}

@Entity(name = "Book")
public static class Book {

	@Id
	private Long id;

	private String title;

	@NaturalId
	private String isbn;

	@ManyToOne
	private Person author;

	//Getters and setters are omitted for brevity

}
```

*Example 297. Incorrect normal Java usage*

```java
Person person = new Person();
person.setName( "John Doe" );

Book book = new Book();
person.getBooks().add( book );
try {
	book.getAuthor().getName();
}
catch (NullPointerException expected) {
	// This blows up ( NPE ) in normal Java usage
}
```

这在正常的Java使用中会爆炸。正确的常规Java用法是：

*Example 298. Correct normal Java usage*

```java
Person person = new Person();
person.setName( "John Doe" );

Book book = new Book();
person.getBooks().add( book );
book.setAuthor( person );

book.getAuthor().getName();
```

```tex
译者注：
mappedBy = "author"
```

字节码增强的双向关联管理通过操纵双向关联的“另一侧”来使第一个示例起作用。



##### Internal performance optimizations

略

##### Runtime enhancement

略

###5.3. Making entities persistent

一旦创建了新的实体实例（使用标准new运算符），它就处于new状态。您可以通过将其关联到org.hibernate.Session或javax.persistence.EntityManager来使其持久化。

*Example 301. Making an entity persistent with JPA*

```java
Person person = new Person();
person.setId( 1L );
person.setName("John Doe");

entityManager.persist( person );
```

*Example 302. Making an entity persistent with Hibernate API*

```java
Person person = new Person();
person.setId( 1L );
person.setName("John Doe");

session.save( person );
```

如果DomesticCat实体类型具有生成的标识符，则在调用save或persist时，该值与实例相关联。如果没有自动生成标识符，则在调用save或persist方法之前，必须在实例上设置手动分配的（通常是自然的）键值。



### 5.4. Deleting (removing) entities

实体也可以删除。

*Example 303. Deleting an entity with JPA*

```java
entityManager.remove( person );
```

*Example 304. Deleting an entity with the Hibernate API*

```java
session.delete( person );
```

```tex
注：
这意味着传递给org.hibernate.Session的delete方法的实体实例可以处于托管状态或分离状态，而传递给javax.persistence.EntityManager的remove方法实体实例必须处于托管状态。
```



### 5.5. Obtain an entity reference without initializing its data

有时称为延迟加载，而无需加载其数据就可以获取对实体的引用的能力非常重要。最常见的情况是需要在一个实体和另一个现有实体之间创建关联。

*Example 305. Obtaining an entity reference without initializing its data with JPA*

```java
Book book = new Book();
book.setAuthor( entityManager.getReference( Person.class, personId ) );
```

*Example 306. Obtaining an entity reference without initializing its data with Hibernate API*

```java
Book book = new Book();
book.setId( 1L );
book.setIsbn( "123-456-7890" );
entityManager.persist( book );
book.setAuthor( session.load( Person.class, personId ) );
```

上面的假设是，通常通过使用运行时代理将实体定义为允许延迟加载。在这两种情况下，如果在应用程序尝试以任何需要访问其数据的方式来使用返回的代理时，如果给定实体未引用实际的数据库状态，则会在以后引发异常。



### 5.6. Obtain an entity with its data initialized

想要与数据一起获得实体也是很普遍的（例如，当我们需要在UI中显示实体时）。

*Example 307. Obtaining an entity reference with its data initialized with JPA*

```java
Person person = entityManager.find( Person.class, personId );
```

*Example 308. Obtaining an entity reference with its data initialized with Hibernate API*

```java
Person person = session.get( Person.class, personId );
```

*Example 309. Obtaining an entity reference with its data initialized using the* `byId()` *Hibernate API*

```java
Person person = session.byId( Person.class ).load( personId );
```

在这两种情况下，如果都找不到匹配的数据库行，则返回null。

也可以返回Java 8 Optional：

```java
Optional<Person> optionalPerson = session.byId( Person.class ).loadOptional( personId );
```



### 5.7. Obtain multiple entities by their identifiers



### 5.8. Obtain an entity by natural-id





## 6. Flushing



## 7. Database access

###7.1. ConnectionProvider

作为ORM工具，您可能需要告诉Hibernate的最重要的事情就是如何连接到数据库，以便它可以代表您的应用程序进行连接。这最终是org.hibernate.engine.jdbc.connections.spi.ConnectionProvider接口的功能。Hibernate提供了该接口的一些现成的实现。 ConnectionProvider也是一个扩展点，因此您还可以使用第三方的自定义实现或自己编写。将ConnectionProvider到使用由定义hibernate.connection.provider_class设置。见org.hibernate.cfg.AvailableSettings#CONNECTION_PROVIDER

一般来说，ConnectionProvider如果使用Hibernate提供的一种实现，则应用程序不必显式配置。Hibernate将ConnectionProvider根据以下算法在内部确定使用哪个：

1. 如果hibernate.connection.provider_class设置，则优先

2. 否则，如果hibernate.connection.datasource已设置→使用数据源

3. 否则，如果设置了前缀hibernate.c3p0.为的任何设置→使用c3p0

4. 否则，如果已设置前缀hibernate.proxool.为任何设置→使用Proxool

5. 否则，如果已设置前缀hibernate.hikari.为→，则使用HikariCP

6. 否则，如果已设置前缀hibernate.vibur.为的任何设置→使用Vibur DBCP

7. 否则，如果通过任何前缀设置hibernate.agroal.设置→使用Agroal

8. 否则，如果hibernate.connection.url已设置→使用Hibernate的内置（且不受支持）池

9. else→用户提供的连接





### 7.2. Using DataSources



### 7.3. Driver Configuration

```tex
hibernate.connection.driver_class
要使用的JDBC Driver类的名称

hibernate.connection.url
JDBC连接网址

hibernate.connection.*
所有此类设置名称（预定义的名称除外）都将hibernate.connection.删除前缀。其余名称和原始值将作为JDBC连接属性传递给驱动程序
```



###7.4. Using c3p0

Hibernate还为应用程序使用c3p0连接池提供支持。启用c3p0支持后，除了“驱动程序配置”中介绍的常规设置外，还会识别许多c3p0特定的配置设置。

连接的事务隔离由其ConnectionProvider本身管理。请参阅7.11

```tex
hibernate.c3p0.min_size 要么 c3p0.minPoolSize
c3p0池的最小大小。参见c3p0 minPoolSize

hibernate.c3p0.max_size 要么 c3p0.maxPoolSize
c3p0池的最大大小。参见c3p0 maxPoolSize

hibernate.c3p0.timeout 要么 c3p0.maxIdleTime
连接空闲时间。参见c3p0 maxIdleTime

hibernate.c3p0.max_statements 要么 c3p0.maxStatements
控制c3p0 PreparedStatement缓存大小（如果使用）。参见c3p0 maxStatements

hibernate.c3p0.acquire_increment 要么 c3p0.acquireIncrement
池耗尽时应获取的连接数c3p0。参见c3p0 acquisitionIncrement

hibernate.c3p0.idle_test_period 要么 c3p0.idleConnectionTestPeriod
验证c3p0池连接之前的空闲时间。参见c3p0 idleConnectionTestPeriod

hibernate.c3p0.initialPoolSize
初始c3p0池大小。如果未指定，则默认为使用最小池大小。参见c3p0 initialPoolSize

其他带有前缀的设置 hibernate.c3p0.
将hibernate.剥离该部分并将其传递给c3p0。

其他带有前缀的设置 c3p0.
照原样传递给c3p0。请参阅c3p0配置
```



### 7.5. Using Proxool

```tex
注：
要使用Proxool集成，应用程序必须在类路径中包括hibernate-proxool模块jar（及其依赖项）。
```

Hibernate还为应用程序使用Proxool连接池提供支持。

连接的事务隔离由其ConnectionProvider本身管理。请参阅7.11

#### Using existing Proxool pools

由hibernate.proxool.existing_pool设置控制。如果设置为true，则此ConnectionProvider将按hibernate.proxool.pool_alias设置指示使用别名使用已经存在的Proxool池。

#### Configuring Proxool via XML

该hibernate.proxool.xml设置将Proxool配置XML文件命名为要作为类路径资源加载并由Proxool的JAXPConfigurator加载。请参阅proxool配置。 hibernate.proxool.pool_alias必须设置以指示要使用哪个池。

#### Configuring Proxool via Properties

该hibernate.proxool.properties设置将Proxool配置属性文件命名为要作为类路径资源加载并由Proxool的加载PropertyConfigurator。请参阅proxool配置。 hibernate.proxool.pool_alias必须设置以指示要使用哪个池。

### 7.6. Using HikariCP

略

### 7.7. Using Vibur DBCP

略

### 7.8. Using Agroal

略

### 7.9. Using Hibernate’s built-in (and unsupported) pooling

Hibernate内置连接池不支持在生产系统中使用。

略

### 7.10. User-provided Connections

略

### 7.11. ConnectionProvider support for transaction isolation setting

所有提供的ConnectionProvider实现，支持对Connections从基础池中获取的所有事务进行隔离设置。hibernate.connection.isolation可以使用以下三种格式之一指定的值：

- 在JDBC级别接受的整数值。

- java.sql.Connection表示您要使用的隔离的常量字段的名称。例如，TRANSACTION_REPEATABLE_READ对于java.sql.Connection#TRANSACTION_REPEATABLE_READ。并非仅JDBC标准隔离级别支持此功能，特定于特定JDBC驱动程序的隔离级别不支持此功能。

- java.sql.Connection常量字段的短名称版本，不带TRANSACTION_前缀。例如，REPEATABLE_READ对于java.sql.Connection#TRANSACTION_REPEATABLE_READ。同样，仅JDBC标准隔离级别支持此功能，特定于特定JDBC驱动程序的隔离级别不支持此功能。



### 7.12. Connection handling

连接处理模式由PhysicalConnectionHandlingMode枚举定义，该 枚举提供以下策略：

```tex
IMMEDIATE_ACQUISITION_AND_HOLD
在Connection为将尽快获取Session被打开并保持到Session被关闭。

DELAYED_ACQUISITION_AND_HOLD
将Connection在需要的时候将尽快收购，然后保持，直到Session被关闭。

DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT
将Connection在需要的时候，执行每个语句后释放将尽快收购。

DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION
的Connection，因为它需要每个交易完成后释放将尽快收购。
```

如果您不想使用默认的连接处理模式，则可以通过hibernate.connection.handling_mode配置属性指定连接处理模式。有关更多详细信息，请查看24.4



### 7.13. Database Dialect

尽管SQL是相对标准化的，但是每个数据库供应商都使用ANSI SQL定义的语法的子集和超集。这称为数据库的方言。Hibernate通过其org.hibernate.dialect.Dialect类和每个数据库供应商的各种子类来处理这些方言的变体。

在大多数情况下，Hibernate将能够通过在引导过程中询问JDBC连接的一些问题来确定要使用的适当方言。



## 8. Transactions and concurrency control

重要的是要理解术语“事务”在持久性和对象/关系映射方面具有许多不同但相关的含义。在大多数用例中，这些定义是一致的，但并非总是如此。

- 它可能是指与数据库的物理事务。

- 它可能是指与持久性上下文相关的事务的逻辑概念。

- 它可能是指原型模式所定义的工作单元的应用程序概念。

### 8.1. Physical Transactions

Hibernate使用JDBC API进行持久化。在Java世界中，有两种定义良好的机制来处理JDBC中的事务：JDBC本身和JTA。Hibernate支持与事务集成和允许应用程序管理物理事务的两种机制。

```tex
jdbc （非JPA应用程序的默认设置）
通过调用来管理交易 java.sql.Connection

jta
通过JTA管理交易。请参阅Java EE引导
```

如果JPA应用程序未提供的设置hibernate.transaction.coordinator_class，则Hibernate将根据持久性单元的事务类型自动构建适当的事务协调器。

如果非JPA应用程序未提供的设置hibernate.transaction.coordinator_class，则Hibernate将jdbc用作默认设置。



### 8.2. JTA configuration



### 8.3. Hibernate Transaction API

Hibernate提供了一个API，可帮助将应用程序与所使用的基础物理事务系统中的差异区分开。基于配置TransactionCoordinatorBuilder，当应用程序使用此事务API时，Hibernate只会做正确的事情。这使您的应用程序和组件可以更方便地移植到不同的环境中。

要使用此API，您需要从Session中获取org.hibernate.Transaction。Transaction允许所有你所期望的正常操作：begin，commit并且rollback，它甚至暴露了一些很酷的方法，如：

```tex
markRollbackOnly
在JTA和JDBC中均可使用。

getTimeout 和 setTimeout
在JTA和JDBC中都可以再次使用。

registerSynchronization
这样，即使在非JTA环境中，也可以注册JTA同步。实际上，在JTA和JDBC环境中，它们Synchronizations都是由Hibernate本地保存的。在JTA环境中，Hibernate将永远只能注册一个单一的带有TransactionManager的Synchronization避免排序问题。
```

让我们看一下在各种环境中使用Transaction API的情况。

*Example 384. Using Transaction API in JDBC*

```java
StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
		// "jdbc" is the default, but for explicitness
		.applySetting( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc" )
		.build();

Metadata metadata = new MetadataSources( serviceRegistry )
		.addAnnotatedClass( Customer.class )
		.getMetadataBuilder()
		.build();

SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
		.build();

Session session = sessionFactory.openSession();
try {
	// calls Connection#setAutoCommit( false ) to
	// signal start of transaction
	session.getTransaction().begin();

	session.createQuery( "UPDATE customer set NAME = 'Sir. '||NAME" )
			.executeUpdate();

	// calls Connection#commit(), if an error
	// happens we attempt a rollback
	session.getTransaction().commit();
}
catch ( Exception e ) {
	// we may need to rollback depending on
	// where the exception happened
	if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
			|| session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
		session.getTransaction().rollback();
	}
	// handle the underlying error
}
finally {
	session.close();
	sessionFactory.close();
}
```

*Example 385. Using Transaction API in JTA (CMT)*

```java
StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
		// "jdbc" is the default, but for explicitness
		.applySetting( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta" )
		.build();

Metadata metadata = new MetadataSources( serviceRegistry )
		.addAnnotatedClass( Customer.class )
		.getMetadataBuilder()
		.build();

SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
		.build();

// Note: depending on the JtaPlatform used and some optional settings,
// the underlying transactions here will be controlled through either
// the JTA TransactionManager or UserTransaction

Session session = sessionFactory.openSession();
try {
	// Since we are in CMT, a JTA transaction would
	// already have been started.  This call essentially
	// no-ops
	session.getTransaction().begin();

	Number customerCount = (Number) session.createQuery( "select count(c) from Customer c" ).uniqueResult();

	// Since we did not start the transaction ( CMT ),
	// we also will not end it.  This call essentially
	// no-ops in terms of transaction handling.
	session.getTransaction().commit();
}
catch ( Exception e ) {
	// again, the rollback call here would no-op (aside from
	// marking the underlying CMT transaction for rollback only).
	if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
			|| session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
		session.getTransaction().rollback();
	}
	// handle the underlying error
}
finally {
	session.close();
	sessionFactory.close();
}
```

*Example 386. Using Transaction API in JTA (BMT)*

```java
StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
		// "jdbc" is the default, but for explicitness
		.applySetting( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta" )
		.build();

Metadata metadata = new MetadataSources( serviceRegistry )
		.addAnnotatedClass( Customer.class )
		.getMetadataBuilder()
		.build();

SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
		.build();

// Note: depending on the JtaPlatform used and some optional settings,
// the underlying transactions here will be controlled through either
// the JTA TransactionManager or UserTransaction

Session session = sessionFactory.openSession();
try {
	// Assuming a JTA transaction is not already active,
	// this call the TM/UT begin method.  If a JTA
	// transaction is already active, we remember that
	// the Transaction associated with the Session did
	// not "initiate" the JTA transaction and will later
	// nop-op the commit and rollback calls...
	session.getTransaction().begin();

	session.persist( new Customer(  ) );
	Customer customer = (Customer) session.createQuery( "select c from Customer c" ).uniqueResult();

	// calls TM/UT commit method, assuming we are initiator.
	session.getTransaction().commit();
}
catch ( Exception e ) {
	// we may need to rollback depending on
	// where the exception happened
	if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
			|| session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
		// calls TM/UT commit method, assuming we are initiator;
		// otherwise marks the JTA transaction for rollback only
		session.getTransaction().rollback();
	}
	// handle the underlying error
}
finally {
	session.close();
	sessionFactory.close();
}
```









## 9. JNDI

略

## 10. Locking

在关系数据库中，锁是指为防止数据在读取和使用之间发生更改而采取的操作。

您的锁定策略可以是乐观的也可以是悲观的。

- 乐观的

  乐观锁定假设多个事务可以完成而不会互相影响，因此事务可以继续进行而不会锁定它们影响的数据资源。在提交之前，每个事务都会验证没有其他事务修改过其数据。如果检查显示有冲突的修改，则提交的事务将回滚。

- 悲观
  悲观锁定假定并发事务会相互冲突，并要求在读取资源后将其锁定，并且仅在应用程序使用完数据后将其解锁。

Hibernate提供了在应用程序中实现两种锁定类型的机制。

###10.1. Optimistic

Hibernate提供了两种不同的机制来存储版本信息，专用版本号或时间戳。

```tex
注：
对于分离的实例，version或timestamp属性永远不能为null。Hibernate会将任何具有空版本或时间戳的实例检测为瞬态，而与您指定的其他未保存值策略无关。声明可空版本或时间戳属性是避免在Hibernate中进行传递重新连接时遇到问题的一种简便方法，特别是在使用分配的标识符或组合键的情况下尤其有用。
```

#### 10.1.1. Mapping optimistic locking

JPA基于版本（顺序数字）或时间戳策略定义了对乐观锁定的支持。要启用这种类型的乐观锁定，只需在javax.persistence.Version定义乐观锁定值的持久属性中添加即可。根据JPA，这些属性的有效类型限于：

- int 要么 Integer

- short 要么 Short

- long 要么 Long

- java.sql.Timestamp

但是，Hibernate甚至允许您使用Java 8日期/时间类型，例如Instant。

Example 387. `@Version` annotation mapping





###10.2. Pessimistic



## 11. Fetching



## 12. Batching



## 13. Caching



## 14. Interceptors and events



## 15. HQL and JPQL