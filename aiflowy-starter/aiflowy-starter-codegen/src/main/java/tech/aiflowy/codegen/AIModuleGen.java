package tech.aiflowy.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.JdbcTypeMapping;
import com.mybatisflex.core.handler.CommaSplitTypeHandler;
import com.zaxxer.hikari.HikariDataSource;

public class AIModuleGen {
    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();

        //注意：url 需添加上 useInformationSchema=true 才能正常获取表的注释
        dataSource.setJdbcUrl("jdbc:mysql://192.168.2.10:3306/aiflowy-v2?useInformationSchema=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        // 设置时间类型为 Date
        JdbcTypeMapping.registerDateTypes();


        //生成 framework-modules/aiflowy-module-ai 下的代码
        GlobalConfig globalConfig = createGlobalConfig();
        Generator moduleGenerator = new Generator(dataSource, globalConfig);
        moduleGenerator.generate();
    }


    public static GlobalConfig createGlobalConfig() {

        String optionsColumns = "options,vector_store_options,model_options";

        //创建配置内容
        GlobalConfig globalConfig = Util.createBaseConfig(optionsColumns);
        globalConfig.setBasePackage("tech.aiflowy.ai");

        globalConfig.setGenerateTable("tb_bot"
                , "tb_bot_api_key", "tb_bot_category"
                , "tb_bot_conversation", "tb_bot_document_collection", "tb_bot_message"
                , "tb_bot_model", "tb_bot_plugin", "tb_bot_recently_used", "tb_bot_workflow"
                , "tb_document", "tb_document_chunk", "tb_document_collection", "tb_document_collection_category", "tb_document_history"
                , "tb_model", "tb_model_provider"
                , "tb_plugin", "tb_plugin_category", "tb_plugin_category_mapping", "tb_plugin_item"
                , "tb_resource", "tb_resource_category"
                , "tb_workflow", "tb_workflow_category", "tb_workflow_exec_result", "tb_workflow_exec_step", "tb_mcp", "tb_bot_mcp"
        );

        String sourceDir = System.getProperty("user.dir") + "/aiflowy-modules/aiflowy-module-ai/src/main/java";
        globalConfig.setSourceDir(sourceDir);


        ColumnConfig tablesColumnConfig = new ColumnConfig();
        tablesColumnConfig.setPropertyType("java.util.List<String>");
        tablesColumnConfig.setTypeHandler(CommaSplitTypeHandler.class);
        tablesColumnConfig.setColumnName("tables");
        globalConfig.setColumnConfig("tb_dev_module", tablesColumnConfig);

        ColumnConfig validRolesColumnConfig = new ColumnConfig();
        validRolesColumnConfig.setPropertyType("java.util.List<String>");
        validRolesColumnConfig.setTypeHandler(CommaSplitTypeHandler.class);
        validRolesColumnConfig.setColumnName("valid_roles");
        globalConfig.setColumnConfig("tb_dev_table_field", validRolesColumnConfig);

        return globalConfig;
    }

}
