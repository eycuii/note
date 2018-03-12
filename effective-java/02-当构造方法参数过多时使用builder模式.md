# 2. 当构造方法参数过多时使用builder模式

> 第三版

​    

## builder 模式

### 简单例子

```java
public class NutritionFacts {
    private final int servingSize; 
    private final int servings; 
    private final int calories; 
    private final int fat; 
    private final int sodium; 
    private final int carbohydrate; 

    public static class Builder {
        // 必填字段
        private final int servingSize;
        private final int servings;

        // 非必填字段 - 设置默认值
        private int calories      = 0;
        private int fat           = 0;
        private int sodium        = 0;
        private int carbohydrate  = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings    = servings;
        }

        public Builder calories(int val) { 
            calories = val;      
            return this;
        }

        public Builder fat(int val) { 
           fat = val;           
           return this;
        }

        public Builder sodium(int val) { 
           sodium = val;        
           return this; 
        }

        public Builder carbohydrate(int val) { 
           carbohydrate = val;  
           return this; 
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    // 构造方法为private，表示只能由内部调用，其他类不能直接调用该类创建新对象。
    private NutritionFacts(Builder builder) {
        servingSize  = builder.servingSize;
        servings     = builder.servings;
        calories     = builder.calories;
        fat          = builder.fat;
        sodium       = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```

NutritionFacts 类是不可变的，所有的参数默认值都在一个地方。builder 的 setter 方法返回 builder 本身，这样调用就可以被链接起来，从而生成一个流畅的API。下面是客户端代码的示例：

```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
	.calories(100).sodium(35).carbohydrate(27).build();
```

​    

### 抽象类例子

Pizza 抽象类：

```java
public abstract class Pizza {
    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}
    final Set<Topping> toppings;
    
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }
        
        abstract Pizza build();
        
        // Subclasses must override this method to return "this"
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone(); // See Item 50
    }
}
```

子类 NyPizza 和 Calzone：

```java
public class NyPizza extends Pizza {
    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override public NyPizza build() {
            return new NyPizza(this);
        }

        @Override protected Builder self() {
            return this;
        }
    }

    private NyPizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}

public class Calzone extends Pizza {
    private final boolean sauceInside;
    
    public static class Builder extends Pizza.Builder<Builder> {
        private boolean sauceInside = false; // Default

        public Builder sauceInside() {
            sauceInside = true;
            return this;
        }
        
        @Override public Calzone build() {
            return new Calzone(this);
        }
        
        @Override protected Builder self() {
            return this; 
        }
    }
    
    private Calzone(Builder builder) {
        super(builder);
        sauceInside = builder.sauceInside;
    }
}
```

客户端代码：

```java
NyPizza pizza = new NyPizza.Builder(SMALL)
        .addTopping(SAUSAGE).addTopping(ONION).build();
Calzone calzone = new Calzone.Builder()
        .addTopping(HAM).sauceInside().build();
```

​    

### 缺点

Builder 模式也有缺点。为了创建对象，首先必须创建它的 builder。虽然创建这个 builder 的成本在实践中不太可能被注意到，但在**性能关键的情况下可能会出现问题**。

而且，builder 模式比伸缩构造方法模式更冗长，因此只有在有足够的参数时才值得使用它，比如四个或更多。但是请记住，如果希望在将来添加更多的参数。但是，如果从构造方法或静态工厂开始，并切换到 builder，当类演化到参数数量失控的时候，过时的构造方法或静态工厂就会面临尴尬的处境。所以，**最好从一开始就创建一个 builder**。