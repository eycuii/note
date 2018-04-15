## 第6章 命令模式（Command Pattern）

​    

把请求封装成对象。

​    

Command 接口：

```java
public interface Command {
	public void execute(); // 执行命令
	public void undo(); // 取消执行
}
```

开灯命令：

```java
public class LightOnCommand implements Command {
	Light light;
  
	public LightOnCommand(Light light) {
		this.light = light;
	}
 
	public void execute() {
		light.on();
	}
 
	public void undo() {
		light.off();
	}
}
```

遥控器（调用者 Invoker）：

```java
public class SimpleRemoteControl {
	Command command;
	Command undoCommand;
 
	public SimpleRemoteControl() {}
 
	public void setCommand(Command cm) {
		command = cm;
	}
 
	public void buttonWasPressed() {
		command.execute();
 		undoCommand = cm;
	}
 
	public void undoButtonWasPressed() {
		undoCommand.undo();
	}
}
```

​    

### Party 模式

同时执行多个命令。

组成一个新的 Command 类，使用队列等存储多个 Command 对象。

如线程队列，日志请求等。