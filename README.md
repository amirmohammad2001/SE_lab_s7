# SE_lab_s7
## استفاده از facade


### کتابخانه Stack
میدانیم که کتابخانه Stack یک کتابخانه با ویژگی های متعدد است ولی ما صرفا از ویژگی های push , pop , peek , size استفاده میکنیم پس یک رابط کاربری تحت عنوان `SimpleStack` مینویسیم که فقط دارای این ویژگی ها باشد.

### کتابخانه Regex

ما در بخش های مختلف کد از  کتابخانه های مرتبط با `regex` استفاده کرده ایم پس ابتدا تلاش میکنیم که برای این کتابخانه پیجیده یک `interface` تحت عنوان `SimpleMatcher` طراحی کنیم.و متد هایی که از این کتابخانه در پروژه استفاده شده است شامل find , group , matches است.

## استفاده از strategy
در تابع `semanticFunction`به هر یک از action ها یک مقدار int داده شده است که با توجه به ورودی ، این مقدار در داخل یک Switch چک میشود و action مورد نظر اعمال میشود.برای اعمل strategy ما یک interface به اسم Action تعریف کرده ایم و برای هر یک از عملیات ها آن را implement کرده ایم . در این رابط تنها یک method وجود دارد به اسم execute که در آن عملیات مورد نظر ، از کلاس codeGenerator استفاده شده است. برای این کار در ابتدا در کانستراکتور کلاس codeGenerator از یک hashMap استفاده شده است که در آن هر عملیات به عدد مپ شده است و در ادامه فقط از قابلیت polymorphism استفاده شده است.

## استفاده از seperate query from modifier
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
## استفاده از self-encapsulated field
در کلاس CodeGenerator تعدادی فیلد private وجود دارد که به طور مستقیم توسط متدهای این کلاس استفاده می‌شوند. می‌توانیم این فیلدها را encapsulate کنیم:
```java
    private Memory memory = new Memory();
    private SimpleStack<Address> ss = new SimpleStack<Address>();
    private SimpleStack<String> symbolStack = new SimpleStack<>();
    private SimpleStack<String> callStack = new SimpleStack<>();
    private SymbolTable symbolTable;
    private HashMap<Integer , Action> actionMap;
```
متدهای getter را اضافه میکنیم:
```java
    private Memory memory = new Memory();
    private SimpleStack<Address> ss = new SimpleStack<Address>();
    private SimpleStack<String> symbolStack = new SimpleStack<>();
    private SimpleStack<String> callStack = new SimpleStack<>();
    private SymbolTable symbolTable;
    private HashMap<Integer , Action> actionMap;

    public Memory getMemory() {
        return memory;
    }

    public SimpleStack<Address> getSs() {
        return ss;
    }

    public SimpleStack<String> getSymbolStack() {
        return symbolStack;
    }

    public SimpleStack<String> getCallStack() {
        return callStack;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public HashMap<Integer , Action> grtActionMap() {
        return actionMap;
    }
```

## دو مورد مختلف غیر از بازآرایی‌های بالا
میتوانیم از استخراج متد extract method استفاده کنیم. همانطور که مشاهده میکنید، کد سه تابع زیر مشابه هستند و میتوانیم به شکل دیگری از آنها استفاده کنیم:
```java
    public void add() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        memory.updateTemp();
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In add two operands must be integer");
        }
        memory.add3AddressCode(Operation.ADD, s1, s2, temp);
        ss.push(temp);
    }

    public void sub() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        memory.updateTemp();
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In sub two operands must be integer");
        }
        memory.add3AddressCode(Operation.SUB, s1, s2, temp);
        ss.push(temp);
    }

    public void mult() {
        Address temp = new Address(memory.getTemp(), varType.Int);
        memory.updateTemp();
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In mult two operands must be integer");
        }
        memory.add3AddressCode(Operation.MULT, s1, s2, temp);
//        memory.saveMemory();
        ss.push(temp);
    }
```
به شکل زیر تبدیل میشود:
```java
    private void arithmeticOperation(Operation op, varType expectedType) {
    Address temp = new Address(memory.getTemp(), expectedType);
    memory.updateTemp();
    Address s2 = ss.pop();
    Address s1 = ss.pop();
    if (s1.varType != expectedType || s2.varType != expectedType) {
        ErrorHandler.printError("Operands must be of type " + expectedType);
    }
    memory.add3AddressCode(op, s1, s2, temp);
    ss.push(temp);
    }

    public void add() {
        arithmeticOperation(Operation.ADD, varType.Int);
    }

    public void sub() {
        arithmeticOperation(Operation.SUB, varType.Int);
    }

    public void mult() {
        arithmeticOperation(Operation.MULT, varType.Int);
    }
```

میتوانیم از Replace Nested Conditional with Guard Clauses استفاده کنیم:
```java
    if (s1.varType != expectedType || s2.varType != expectedType) {
        ErrorHandler.printError("Operands must be of type " + expectedType);
    }
```

بعد از تغییر به شکل زیر در می آید:
```java
    if (s1.varType != expectedType || s2.varType != expectedType) {
        throw new IllegalArgumentException("Operands must be of type " + expectedType);
    }
```
## questions 
### سوال اول
کد تمیز: کدی است که خوانا، ساده، قابل فهم و قابل نگهداری باشد و هدف آن به‌وضوح مشخص باشد.

بدهی فنی: تصمیمات سریع یا موقتی در توسعه که بعدها نیاز به بازنگری یا اصلاح دارند و در صورت نادیده گرفتن، کیفیت سیستم را پایین می‌آورند.

بوی بد کد (Code Smell): نشانه‌هایی در کد که به وجود مشکلات ساختاری یا طراحی ضعیف اشاره دارند، حتی اگر کد به‌درستی کار کند.

### سوال دوم 
انباشتگی (Bloaters): زمانی رخ می‌دهد که کلاس‌ها یا متدها بیش از حد بزرگ و پیچیده شوند، مانند متدهای طولانی یا داده‌های موقت اضافی.

سوء‌استفاده از شی‌گرایی (Object-Orientation Abusers): شامل مواردی مانند استفاده نادرست از ارث‌بری، متدهای غیرداخل‌کلاس (Feature Envy) یا متدهای تابعی است که مفاهیم شی‌گرایی را نقض می‌کنند.

مانع تغییر (Change Preventers): این دسته زمانی رخ می‌دهد که اعمال یک تغییر کوچک در یک بخش، نیاز به تغییرات در بخش‌های متعدد و غیرمرتبط دیگر دارد.

اضافه‌ها (Dispensables): شامل کدهایی است که بودنشان ضرورتی ندارد و حذف آن‌ها باعث ساده‌تر و تمیزتر شدن کد می‌شود، مانند کدهای مرده یا توضیحات بی‌مورد.

وابستگی شدید (Couplers): زمانی اتفاق می‌افتد که کلاس‌ها یا ماژول‌ها بیش از حد به یکدیگر وابسته هستند، به‌گونه‌ای که تغییر در یکی نیازمند تغییر در دیگری است.

### سوال سوم
به این معنی است که یک کلاس بیش از حد از داده‌ها یا توابع کلاس‌های دیگر استفاده میکند و این مشکل رخ میدهد. این مورد در دسته couplers قرار میگیرد.

برای برطرف کردن میتوانیم از یکی از موارد زیر استفاده کنیم:
1. انتقال تابع : اگر تابعی در کلاس A بیش از حد به داده‌های کلاس B وابسته است، آن تابع را به کلاس B منتقل میکنیم.
2. استخراج تابع : بخش‌هایی از کد که به کلاس دیگر وابسته‌اند را جدا کرده و در آن کلاس قرار میدهیم.

اصولا در مواقع زیر میتوانیم آن را نادیده بگیریم:
1. وقتی کد خوانایی بیشتری دارد و انتقال آن باعث پیچیده‌تر شدن آن شود.
2. در مواردی که وابستگی به کلاس دیگر منطقی است.
3. در پروژه‌های کوچک و اسکریپت‌های موقتی که نیازی به رعایت این موارد نداریم.

### سوال چهارم
تفاوت اول در ماهیت مشکل است؛ بوی بد یک نشانه از طراحی یا ساختار ضعیف کد است که ممکن است در آینده منجر به بروز خطا یا سختی در نگهداری شود، اما الزاماً باعث رفتار نادرست برنامه نمی‌شود اما باگ یک خطای واقعی در منطق یا پیاده‌سازی کد است که باعث می‌شود برنامه نتیجه اشتباه بدهد یا به درستی اجرا نشود.

تفاوت دوم در زمان تشخیص است؛ بوی بد معمولا در  بررسی‌های کد یا تحلیل کیفیت کد شناسایی می‌شود و ممکن است در هنگام اجرای برنامه هیچ مشکلی ایجاد نکند اما باگ معمولا در زمان اجرا یا تست نرم افزار مشخص میشود و رفتار نادرست یا کرش کردن برنامه را دارد.

### سوال پنجم
کد src/com/project/phase2CodeGeneration/Phase2CodeFileManipulator.java را در نظر بگیرید.
1. long method
متدهایی مانند متد generatePhase2, methodCallHandler  طولانی هستند و مسئولیت‌های متعددی دارند و باعث دشواری در درک و تست می‌شوند.

2. large class
کلاس Phase2CodeFileManipulator بزرگ است و وظایف مختلفی را انجام می‌دهد.

3. Primitive Obsession
استفاده از مقادیری مانند int برای مواردی چون depthOfParenthesis و depthOfCurlyBracket می‌تواند با کلاس‌های انتزاعی‌تر یا enumهای جدید جایگزین شود.

4. Switch Statements
در متد generatePhase2 از یک بلوک بزرگ switch استفاده شده است.

5. Divergent Change
هر تغییر در نحوه مدیریت توکن‌ها یا ساختار کلاس‌ها نیازمند تغییرات گسترده در متدهای متعدد مانند generatePhase2 و متدهای مرتبط است.

6. Comments
وجود کامنت‌هایی که صرفاً توضیح‌دهنده‌ی عملکرد مستقیم کد هستند، نشانه‌ای هستند مبنی بر اینکه کد گنگ بوده و خوانایی کافی را ندارد.

7. Dead Code
کدهای کامنت‌شده و بخش‌هایی که دیگر مورد استفاده قرار نمی‌گیرند، باید حذف شوند.

8. Speculative Generality
متدها و مقادیر خاصی وجود دارند که به نظر می‌رسد برای کاربردهای احتمالی آینده طراحی شده‌اند اما در حال حاضر استفاده‌ای ندارند.

9. Feature Envy
برخی متدها مانند generatePhase2 بیش از حد به جزئیات داخلی DiagramInfo و LexicalAnalyzer وابسته هستند. این وابستگی‌ها را می‌توان با انتقال برخی وظایف به این کلاس‌ها کاهش داد.

10. Message Chains
دسترسی زنجیره‌ای به متدها و مقادیر، مانند کد زیر، می‌تواند نشان‌دهنده‌ی وابستگی زیاد به ساختار داخلی کلاس‌های دیگر باشد.
```java
    diagramInfo.isHaveDestructor(attribute.getValueType().getTypeName())
```


### سوال ششم
formatter ابزاری یا پلاگینی در محیط‌های توسعه (IDE) یا ویرایشگرهای کد است که وظیفه دارد کد را به صورت خودکار براساس استانداردهای مشخص قالب‌بندی کند. این ابزارها تنظیماتی برای مرتب‌سازی، فاصله‌گذاری (indentation)، محل قرارگیری پرانتزها و سایر موارد ظاهری کد دارند. این ابزار باعث بهبود خوانایی کد، کاهش خطاهای انسانی، افزایش بهره وری و همچنین تطبیق با استانداردهای تیمی می شود. بنابراین این ابزار باعث می شود در زمان انجام بازآرایی کد به صورت خودکار ساختار جدید، یکنواخت و خوانا شود.