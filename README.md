# SE_lab_s7
## استفاده از Facade refactoring
### کتابخانه Stack
میدانیم که کتابخانه Stack یک کتابخانه با ویژگی های متعدد است ولی ما صرفا از ویژگی های push , pop , peek , size استفاده میکنیم پس یک رابط کاربری تحت عنوان `SimpleStack` مینویسیم که فقط دارای این ویژگی ها باشد.
### کتابخانه Regex
ما در بخش های مختلف کد از  کتابخانه های مرتبط با `regex` استفاده کرده ایم پس ابتدا تلاش میکنیم که برای این کتابخانه پیجیده یک `interface` تحت عنوان `SimpleMatcher` طراحی کنیم.و متد هایی که از این کتابخانه در پروژه استفاده شده است شامل find , group , matches است.

### استفاده از strategy
در تابع `semanticFunction`به هر یک از action ها یک مقدار int داده شده است که با توجه به ورودی ، این مقدار در داخل یک Switch چک میشود و action مورد نظر اعمال میشود.برای اعمل strategy ما یک interface به اسم Action تعریف کرده ایم و برای هر یک از عملیات ها آن را implement کرده ایم . در این رابط تنها یک method وجود دارد به اسم execute که در آن عملیات مورد نظر ، از کلاس codeGenerator استفاده شده است. برای این کار در ابتدا در کانستراکتور کلاس codeGenerator از یک hashMap استفاده شده است که در آن هر عملیات به عدد مپ شده است و در ادامه فقط از قابلیت polymorphism استفاده شده است.

### استفاده از Self-encapsulate field
همانطور که در کد زیر مشاهده میکنید تابع getTemp علاوه بر دادن یک مقدار ، مقداری را نیز تغییر میدهد.
```java
    public int getTemp() {
        lastTempIndex += tempSize;
        return lastTempIndex - tempSize;
    }
```
پس کد بالا را به کد زیر تبدیل میکنیم و با هر با صدا زده شدن کد بالا از دو کد زیر استفاده میکنیم.
```java
    public int getTemp() {
        return lastTempIndex;
    }

    public void updateTemp() {
        lastTempIndex += tempSize;
    }
```