# 提示词（Prompt）工程指南

本章节旨在系统化介绍Prompt的概念、原理及高效使用方法，帮助用户实现更优质的人机交互体验。

## Prompt基础定义

### 1.什么是Prompt？

- **直译**：提示词/提示符（人类与AI大语言模型的交互语言）
- **技术类比**：
  | 对象       | 执行单元       | 沟通媒介   |
  |------------|---------------|------------|
  | 程序员      | CPU           | 代码       |
  | AI使用者    | 大语言模型     | Prompt     |

### 2.Prompt的核心价值
✅ 降低AI使用门槛  
✅ 提升模型输出准确性  
✅ 释放大模型深层能力（如复杂推理）

## Prompt设计原理

### 1. 学术研究启示
| 论文标题                                      | 核心发现                                                                 | 论文链接                                  |
|-----------------------------------------------|--------------------------------------------------------------------------|-------------------------------------------|
| *Large Language Models are Zero-Shot Reasoners* | 添加"Let's think step by step"可使推理准确率提升60%+                     | [arXiv:2205.11916](https://arxiv.org/abs/2205.11916) |
| *Large Language Models Are Human-Level Prompt Engineers* | 自动生成的Prompt效果可超越人工设计                                      | [项目主页](https://sites.google.com/view/automatic-prompt-engineer) |

### 2. 关键技巧
**思维链（Chain of Thought）**
```python
# 低效Prompt
"法国的首都是哪里？"

# 高效Prompt
"""
请按步骤思考：
1. 法国是一个欧洲国家
2. 需要确定其行政中心
3. 根据地理知识得出结论
法国的首都是哪里？
"""
```

# 智能体 Agent

## 智能体 Agent

2023 年底，OpenAI 的创始人奥特曼在 OpenAI 开发者大会上表示：
> 在未来的每个行业，每个人都会拥有一个属于自己的 AI 智能体。

微软的比尔·盖茨也为 AI 智能体撰写了长文，指出 AI 智能体将彻底改变传统的人机交互方式，并颠覆整个软件行业。正是从这个时候开始，我深入思考了软件的未来，以及公司全面拥抱 AI 的战略决策。

---

### 1.什么是 AI 智能体？

**AI 智能体** 是一个抽象的概念，目前业内有许多不同的定义和解读。

- **OpenAI 的团队负责人 Lilian Weng 提出：**
  > 智能体 = LLM（大语言模型） + 记忆 + 规划技能 + 工具使用。

- **2024 年红杉资本人工智能峰会上，吴恩达提出：**  
  智能体应具备以下四种能力：
    1. 反思（Reflection）
    2. 使用工具（Tool use）
    3. 规划（Planning）
    4. 多智能体协同（Multi-agent collaboration）

这些概念虽然抽象，但笔者认为：  
**智能体就像一个人——有身份、有姓名、有记忆、能执行。**  
在执行过程中，通过大语言模型的能力，可以进行规划（Planning）、调用工具（Tools），或者进行批判性思考（Critical Thinking）。  
至于多智能体协同，则是根据每个智能体的能力，由程序员对其进行组合与编排，从而实现整体协作的目标。

---

### 2.智能体的架构设计

在 **AIFlowy** 的底层框架 **Agents-Flex** 中，`Agent` 是这样定义的：
```java
public abstract class Agent {
    private Object id;
    private String name;
    private ContextMemory memory;

    public abstract Output execute(Map<String, Object> variables, Chain chain);
}
```

- **id**: 定义了智能体的 id，也就是智能体的身份。
- **name**: 智能体的姓名（或别名）。
- **memory**: 记忆存储器。
- **execute(Map<String, Object> variables, Chain chain)**: 执行方法，由子类实现。

### 3.示例代码

以下示例代码展示了一个最初级的智能体 `Text2DdlAgent`，它能够将人类自然语言描述的 DDL 转换为可执行的 DDL 语句：

```java
public class Text2DdlAgent extends LLMAgent {

    public Text2DdlAgent(Llm llm) {
        this.llm = llm;
        this.prompt = "您现在是一个 MySQL 数据库架构师，请根据如下的表结构信息，" +
                "帮我生成可以执行的 DDL 语句，以方便我用于创建 MySQL 的表结构。\n" +
                "注意：\n" +
                "请直接返回 DDL 内容，不需要解释，不需要以及除了 DDL 语句以外的其他内容。\n" +
                "\n以下是表信息的内容：\n\n{ddlInfo}";
    }

    @Override
    protected Output onMessage(AiMessage aiMessage) {
        String sqlContent = aiMessage.getContent()
                .replace("```sql", "")
                .replace("```", "");
        return Output.ofValue(sqlContent);
    }
}
```

以上的示例代码，我们可以理解为一个最初级的智能体，它能够通过 ddl 描述的自然语言，转换为一个 可以执行的 DDL 语句。以下是执行代码示例：

```java
public static void main(String[] args) {
    Llm llm = new OpenAiLlm.of("sk-rts5NF6n*******");

    SampleLlmAgent agent = new SampleLlmAgent(llm);

    String ddlInfo = "表名 student，字段 id,name";

    Map<String, Object> variables = new HashMap<>();
    variables.put("ddlInfo", ddlInfo);

    Output output = agent.execute(variables, null);
    System.out.println(output.getValue());
}
```

我们给出了人类自然语言：表名 student，字段 id,name ，智能体执行完成后，输出的内容如下：

```sql
CREATE TABLE `student` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `name` VARCHAR(255) COMMENT '学生姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

这个 Text2DdlAgent是一个简单的示例，体现了其执行并调用工具的过程：首先是通过大语言模型，通过用户传入的 ddlInfo 自然语言，转换为大语言模型的输出，最后调用工具，把大语言模型的输出过滤掉其他内容，只提取了 DDL 内容。

在以上的例子中，只体现了一个最为简单的智能体的流程，在复杂的场景中，比如在某些业务查询下，我们可能需要 text2sql，也就是把人类的自然语言转换为 sql，然后再去数据库查询数据。
那么在这个过程中，可能就会涉及到很多的安全问题，比如黑客会通过特定的语言，让大语言输出带有 delete 等危险操作的 SQL，那么这个时候，我们就需要用到多智能体协作，以及吴恩达老师提到的智能体反思（Reflection）等能力。这些知识内容，我们在 《执行链 Chain》 章节会有讲解。

### 4.关于智能体设计的一些思考？

就目前而已，虽然已经提出了 “智能体” 的概念，但是都是一些非常抽象的概念，关于智能体应该有哪些属性、哪些具体的能力，并没有统一的定论。
Agents-Flex 可能是世界上第一个拥有智能体定义的技术开发框架，因此我们无法从技术其他框架上获得指导以及参考（目前比如 LangChain 可能会有一些抽象概念，但为对其进行任何的定义）。
Agent 这个单词翻译过来，是代理，代理人的含义，我更愿意把他看做一个 “代理人”，ta除了能处理某些事情以外，作为一个 “人”，其必须有 id （对比人类的身份证号码）和姓名。
这有许多好处，我们人类作为一个智能体的统一调度者，我们可以通过姓名和 id 方便的去指挥、和编排智能体。我们为不同能力的智能体赋予不同的名字，也更好的方便我们正在上帝视角来俯视和理解智能体。

所以，在 Agents-Flex 框架中，Agent Class 的定义如下：

```java
public abstract class Agent {
private Object id;
private String name;

    //其他属性...

    //执行方法
    public abstract Output execute();
}
```

有 id、姓名以及执行方法，Agent 有多种不同的子类，比如 LLMAgent，其实就是 LLM （大语言模型）的能力进行执行，然后调用工具方法对执行结果进行进一步处理，或者数据提取。


# Function Calling

## Function Calling

### 1.什么是 Function Calling
Function Calling 指的是，用户在传入提示词 prompt 给大模型时，同时传入方法以及参数的定义信息，大模型会理解用户的提示词，返回应该调用哪个方法，以及返回该方法的对应参数。我们得到大模型返回的信息后，主动通过大模型返回的参数，在本地调用本地方法，得到执行的结果。最后使用该结果进行输出。


以 openai 的 chatGPT 为例，我们需要调用其 Function calling API，并传入如下内容：
通过这个 json 信息，我们可以看出，用户进行了提问 What's the weather like in Boston today? 并传入了一个方法名称 get_current_weather ，以及这个方法的描述，以及相关参数的定义及其描述。
此时，openai 返回的内容如下：
返回的内容，告知了我们应该调用哪个方法，以及对应的参数 location 的值为 Boston，此时，我们就可以在本地调用 get_current_weather方法，得到执行结果。
AIFlowy 的 Function Calling
在以上的示例中，我们通过底层逻辑讲清楚了其原理，AIFlowy 就是基于以上的原理，进一步进行封装，通过几个注解使得我们的 java 方法可以转换为调用的 json 内容。同时在返回的结果中，我们可以通过返回的 json 内容执行本地方法得到结果。
### 2.示例代码
第一步：我们需要定义一个方法，如下代码所示：

```java
public class WeatherUtil {

    @FunctionDef(name = "get_current_weather", description = "get the weather info")
    public static String getWeatherInfo(
        @FunctionParam(name = "city", description = "the city name") String name) 
    {
        //在这里，我们应该通过第三方接口调用 api 信息
        return name + "的天气是阴转多云。 ";
    }
}
```

在以上的代码中，我们使用到了两个注解，分别是：
@FunctionDef 用于定义方法的名称以及描述
@FunctionParam 用于定义参数的名称以及描述
第二步：我们需要创建一个 FunctionPrompt 并传入提示词 prompt，以及 WeatherUtil 类，通过执行 chat 方法，得到 FunctionResultResponse, 最后调用其 invoke() 方法得到结果。如下代码所示：

```java
public static void main(String[] args) {
OpenAiLlmConfig config = new OpenAiLlmConfig();
config.setApiKey("sk-rts5NF6n*******");

    OpenAiLlm llm = new OpenAiLlm(config);

    FunctionPrompt prompt = new FunctionPrompt("今天北京的天气怎么样", WeatherUtil.class);
    FunctionResultResponse response = llm.chat(prompt);

    //执行工具类方法得到结果
    Object result = response.invoke();

    System.out.println(result);
    //"北京的天气是阴转多云。 "
}
```

在以上的代码中，AIFlowy 的底层框架 AgentsFlex 会自动把 WeatherUtil.getWeatherInfo方法转换为大模型要求的 json 内容，以此同时，大模型响应的内容自动被封装到 FunctionResultResponse中，我们只需要调用 FunctionResultResponse.invoke() 方法，就会立即调用本地方法  WeatherUtil.getWeatherInfo 并得到结果。

