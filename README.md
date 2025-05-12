# BatuBook

### Full Stack Software Project with Java, Spring Boot, React.js, PostgreSQL, Postman, JUnit.  

BatuBook, kullanıcıların kitaplar ile etkileşime geçebileceği, kitapları inceleyebileceği, alıntılar yapabileceği, yorum bırakabileceği ve diğer kullanıcılarla etkileşimde bulunabileceği bir platformdur. Bu platform, modern yazılım geliştirme pratiklerine uygun şekilde Spring Boot ile geliştirilmiştir. API tabanlı bir yapıya sahip olup, kullanıcılar ve kitaplar arasındaki tüm etkileşimler RESTful servisler üzerinden sağlanmaktadır.

## Proje Yapısı

BatuBook, katmanlı mimari kullanılarak geliştirilmiştir. Proje yapısı aşağıdaki ana bileşenlerden oluşmaktadır:

- **Entity**: Veritabanı ile etkileşim sağlayan sınıflar olup, her biri veritabanındaki bir tabloyu temsil eder.
- **DTO (Data Transfer Object)**: İstemci ve sunucu arasındaki veri taşımak için kullanılan veri yapılarıdır. Bu katman, entity ile doğrudan etkileşimi engeller ve yalnızca gerekli verilerin taşınmasını sağlar.
- **Mapper**: Entity ve DTO arasında veri dönüşümü sağlamak için kullanılan sınıflardır.
- **Repository**: Veritabanı işlemlerini gerçekleştiren katmandır. Spring Data JPA ile veritabanı sorguları ve işlemleri gerçekleştirilir.
- **Service**: İş mantığının yer aldığı katmandır. Repository'den gelen veriler burada işlenir ve Controller katmanına iletilir.
- **Controller**: Kullanıcıdan gelen HTTP isteklerini yöneten katmandır. RESTful API endpoint'lerini içerir ve Service katmanıyla etkileşimde bulunur.

## Kullanılan Teknolojiler

BatuBook projesinde aşağıdaki teknolojiler kullanılmıştır:

- **Spring Boot**: Uygulamanın temel yapılandırması ve iş mantığı için kullanılmıştır.
- **Spring Data JPA**: Veritabanı ile etkileşim için kullanılır. Repository katmanındaki CRUD işlemleri Spring Data JPA tarafından otomatik sağlanır.
- **HikariCP**: Bağlantı havuzu yönetimi için kullanılır ve yüksek performans sağlar.
- **Spring Security**: Uygulama güvenliğini sağlamak amacıyla kimlik doğrulama ve yetkilendirme işlemleri gerçekleştirilmiştir.
- **BCryptPasswordEncoder**: Kullanıcı şifrelerini güvenli bir şekilde saklamak için şifreleme algoritması olarak kullanılmıştır.
- **JUnit 5**: Test yazımı ve birim testler için kullanılmıştır.
- **Mockito**: Mocking işlemleri için kullanılarak, servis ve repository katmanları için bağımsız testler sağlanmıştır.
- **PostgreSQL**: Veritabanı olarak kullanılmıştır.

## Proje Mimarisi

- **Controller**: HTTP isteklerini alır ve ilgili servis metotlarına yönlendirir. Controller'lar RESTful API endpoint'leri sağlar.
- **Service**: İş mantığının bulunduğu katmandır. Controller'dan gelen veriler burada işlenir ve gerektiğinde Repository katmanından veri çekilir.
- **Repository**: Veritabanı ile iletişim kuran katmandır. Veritabanı sorguları burada gerçekleştirilir.
- **Entity**: Veritabanındaki tabloları temsil eder ve her bir entity sınıfı, veritabanında bir tabloya karşılık gelir.
- **DTO (Data Transfer Object)**: Entity sınıflarının taşınabilir versiyonlarıdır. Kullanıcıya gösterilecek veriler genellikle DTO'lar üzerinden taşınır.
- **Mapper**: Entity ve DTO arasında dönüşüm yapılmasını sağlayan yardımcı sınıflardır.
- **Exception Handling**: Uygulamanın tüm seviyelerinde hata yönetimi için özel exception sınıfları ve hata dönüşüm mekanizmaları kullanılmaktadır.
- **Database Configuration**: Uygulamanın veritabanı bağlantısı için HikariCP kullanılmış ve Spring Profile üzerinden test ve üretim ortamlarına özgü ayarlar yapılmıştır.
- **Security**: Uygulamanın güvenliği için Spring Security kullanılmış ve kullanıcı kimlik doğrulaması, yetkilendirmesi yapılmıştır. CSRF koruması devre dışı bırakılmıştır.

## Proje Özellikleri

- **Kullanıcı Yönetimi**: Kullanıcı kaydı, girişi ve şifre yönetimi işlemleri yapılabilir. Kullanıcı profili güncellenebilir.
- **Kitap Yönetimi**: Kitaplar eklenebilir, güncellenebilir, silinebilir ve listelenebilir. Kullanıcılar kitaplara yorum ve alıntılar ekleyebilir.
- **Kitap Etkileşimleri**: Kullanıcılar kitaplarla etkileşime geçebilir, kitapları beğenebilir, yorum yapabilir ve alıntılar oluşturabilir.
- **Takip Sistemi**: Kullanıcılar birbirlerini takip edebilir ve takip ettikleri kullanıcıların etkileşimlerini görüntüleyebilir.
- **Alıntı ve Yorum Sistemi**: Kullanıcılar, kitaplardan alıntılar yapabilir ve kitaplar hakkında yorumlar bırakabilir.
- **Yetkilendirme ve Kimlik Doğrulama**: Spring Security ile kullanıcıların sadece yetkili oldukları işlemleri gerçekleştirmesi sağlanır.

## Projenin Ekran Görüntüleri

### Intellij Idea Run

![IntelliJ IDEA Çalıştırma](https://github.com/BatuhanBaysal/BatuBook/raw/master/intellij-idea-run.PNG)

### Intellij Idea Test Run

![IntelliJ IDEA Test Çalıştırma](https://github.com/BatuhanBaysal/BatuBook/raw/master/intellij-idea-test-run.PNG)

### Postman Run

![Postman Çalıştırma](https://github.com/BatuhanBaysal/BatuBook/raw/master/postman-run.PNG)

### Intellij Idea Test Run

![DBeaver - PostgreSQL Çalıştırma](https://github.com/BatuhanBaysal/BatuBook/raw/master/dbeaver-postgresql-run.PNG)

## Kurulum ve Çalıştırma

### Gerekli Araçlar

- JDK 17
- Maven 3
- DBeaver / PostgreSQL veritabanı
- Postman
- IntelliJ IDEA (IDE)

### Kurulum Adımları

#### 1. Projeyi Klonla

```bash
git clone https://github.com/BatuhanBaysal/BatuBook.git
```

#### 2. Bağımlılıkları Yükle

```bash
mvn clean install
```

#### 3. Veritabanı Konfigürasyonu

- application.properties ve application-test.properties dosyasındaki veritabanı bağlantı bilgilerini düzenlemek için:

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/batubook
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```

#### 4. Uygulamayı Çalıştır

```bash
mvn spring-boot:run
```

## Testler

BatuBook, JUnit 5 ve Mockito kullanılarak kapsamlı bir şekilde test edilmiştir. Proje içerisindeki testleri çalıştırmak için:

#### Testleri Çalıştır

- Maven:

   ```bash
   mvn test
   ```

## İletişim (Contact)

- **LinkedIn**: [Batuhan Baysal LinkedIn Profilim](https://www.linkedin.com/in/batuhan-baysal-502656170/)

- **Github**: [Batuhan Baysal GitHub Profilim](https://github.com/BatuhanBaysal)
   
