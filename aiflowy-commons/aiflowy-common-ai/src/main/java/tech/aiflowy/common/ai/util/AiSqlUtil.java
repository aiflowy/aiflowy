package tech.aiflowy.common.ai.util;

import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.StringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

import java.math.BigInteger;
import java.util.*;

public class AiSqlUtil {


    public static List<String> getTablesFormDDL(String sql) {
        String[] sqls = sql.split(";");
        List<String> tableNames = new ArrayList<>();
        for (String sqlPart : sqls) {
            if (tech.aiflowy.common.util.StringUtil.hasText(sqlPart.trim())) {
                try {
                    CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlPart.trim());
                    tableNames.add(createTable.getTable().getName().replace("`", ""));
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableNames;
    }


    /**
     * 自动修改主键的名称为 id
     *
     * @param ddl 创建表的 ddl
     * @return 返回新的 ddl
     */
    public static String changePrimaryNameToId(String ddl) {
        StringBuilder result = new StringBuilder();
        String[] sqls = ddl.split(";");
        for (String sqlPart : sqls) {
            if (tech.aiflowy.common.util.StringUtil.hasText(sqlPart.trim())) {
                try {
                    CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlPart.trim());
                    String pkName = null;
                    List<Index> indexes = createTable.getIndexes();
                    if (indexes != null) {
                        for (Index index : indexes) {
                            if (pkName == null && "PRIMARY KEY".equalsIgnoreCase(index.getType())) {
                                List<Index.ColumnParams> columns = index.getColumns();
                                for (Index.ColumnParams column : columns) {
                                    String columnName = column.columnName;
                                    pkName = columnName.replace("`", "");
                                    index.setColumns(Collections.singletonList(new Index.ColumnParams("`id`")));
                                }
                            }
                        }
                    }
                    List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
                    if (columnDefinitions != null) {
                        for (ColumnDefinition columnDefinition : columnDefinitions) {
                            String columnName = columnDefinition.getColumnName().replace("`", "");
                            if ((pkName != null && pkName.equalsIgnoreCase(columnName))
                                    || (containsIgnoreCase(columnDefinition.getColumnSpecs(), "PRIMARY"))) {
                                columnDefinition.setColumnName("`id`");

                                //设置数据类型为 bigint(20)
                                ColDataType colDataType = new ColDataType("BIGINT");
                                colDataType.addArgumentsStringList("20");
                                columnDefinition.setColDataType(colDataType);

                                //设置主键为 UNSIGNED
                                List<String> columnSpecs = columnDefinition.getColumnSpecs();
                                if (containsIgnoreCase(columnSpecs, "UNSIGNED")) {
                                    columnSpecs.add(0, "UNSIGNED");
                                }

                                //移除自增行为
                                if (columnSpecs != null) {
                                    columnSpecs.removeIf("AUTO_INCREMENT"::equalsIgnoreCase);
                                }
                            }
                        }
                    }
                    String sql = createTable.toString();
                    result.append(sql).append(";");
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result.toString();
    }


    private static boolean containsIgnoreCase(Collection<String> strings, String string) {
        if (strings == null || strings.isEmpty()) {
            return false;
        }

        for (String s : strings) {
            if (s != null && s.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }


    public static String removeForeignKeys(String ddl) {
        StringBuilder result = new StringBuilder();
        String[] sqls = ddl.split(";");
        for (String sqlPart : sqls) {
            if (tech.aiflowy.common.util.StringUtil.hasText(sqlPart.trim())) {
                try {
                    CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlPart.trim());
                    List<Index> indexes = createTable.getIndexes();
                    if (indexes != null) {
                        indexes.removeIf(index -> "FOREIGN KEY".equalsIgnoreCase(index.getType()));
                    }
                    String sql = createTable.toString();
                    result.append(sql).append(";");
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result.toString();
    }


    public static String appendTablePrefix(String ddl, String tablePrefix) {
        StringBuilder result = new StringBuilder();
        String[] sqls = ddl.split(";");
        for (String sqlPart : sqls) {
            if (tech.aiflowy.common.util.StringUtil.hasText(sqlPart.trim())) {
                try {
                    CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlPart.trim());
                    Table table = createTable.getTable();
                    String newTableName = table.getName();
                    if (newTableName.startsWith("`")) {
                        newTableName = "`" + tablePrefix + newTableName.substring(1);
                    } else {
                        newTableName = tablePrefix + newTableName;
                    }
                    table.setName(newTableName);
                    String sql = createTable.toString();
                    result.append(sql).append(";");
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result.toString();
    }

    public static String makeColumnsAllowNullAndUnderlineNamed(String ddl) {
        StringBuilder result = new StringBuilder();
        String[] sqls = ddl.split(";");
        for (String sqlPart : sqls) {
            if (tech.aiflowy.common.util.StringUtil.hasText(sqlPart.trim())) {
                try {
                    CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlPart.trim());
                    String pkName = null;
                    List<Index> indexes = createTable.getIndexes();
                    if (indexes != null) {
                        for (Index index : indexes) {
                            if (pkName == null && "PRIMARY KEY".equalsIgnoreCase(index.getType())) {
                                List<Index.ColumnParams> columns = index.getColumns();
                                for (Index.ColumnParams column : columns) {
                                    String columnName = column.columnName;
                                    pkName = columnName.replace("`", "");
                                }
                            }
                        }
                    }

                    List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
                    if (columnDefinitions != null) {
                        for (ColumnDefinition columnDefinition : columnDefinitions) {
                            String columnName = columnDefinition.getColumnName().replace("`", "");
                            if ((pkName != null && pkName.equalsIgnoreCase(columnName))
                                    || (containsIgnoreCase(columnDefinition.getColumnSpecs(), "PRIMARY"))) {
                                continue;
                            }

                            columnDefinition.setColumnName("`" + StringUtil.camelToUnderline(columnName) + "`");


                            String dataType = columnDefinition.getColDataType().getDataType();
                            if (dataType.equalsIgnoreCase("text")) {
                                continue;
                            }

                            List<String> columnSpecs = columnDefinition.getColumnSpecs();
                            if (columnSpecs == null || columnSpecs.isEmpty()) {
                                columnDefinition.addColumnSpecs("DEFAULT", "NULL");
                                continue;
                            }

                            if (columnSpecs.contains("DEFAULT")) {
                                continue;
                            }

                            if (columnSpecs.contains("NOT") && columnSpecs.contains("NULL")) {
                                columnSpecs.remove("NOT");
                                columnSpecs.remove("NULL");
                            }

                            if (columnSpecs.contains("NULL")) {
                                continue;
                            }

                            columnSpecs.add(0, "DEFAULT");
                            columnSpecs.add(1, "NULL");
                        }
                    }
                    String sql = createTable.toString();
                    result.append(sql).append(";");
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result.toString();
    }


    public static void main(String[] args) {
        String sql = "CREATE TABLE `students` (\n" +
                "  `student_id` INT(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(255) DEFAULT NULL COMMENT '姓名',\n" +
                "  `gender` ENUM('男', '女') COMMENT '性别',\n" +
                "  `birthdate` DATE NOT NULL COMMENT '出生日期',\n" +
                "  `classId` INT(11) COMMENT '班级编号',\n" +
                "  PRIMARY KEY (`student_id`)\n" +
                ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;\n" +
                "CREATE TABLE `classes` (\n" +
                "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(255) COMMENT '班级名称',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;\n" +
                "CREATE TABLE grades (\n" +
                "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `studentId` INT(11) COMMENT '学号',\n" +
                "  `course_id` INT(11) COMMENT '课程编号',\n" +
                "  `score` DECIMAL(5, 2) COMMENT '成绩',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;\n" +
                "CREATE TABLE `courses` (\n" +
                "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(255) COMMENT '课程名称',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;";

        String result = changePrimaryNameToId(sql);

//        List<String> tablesFormSql = getTablesFormDDL(sql);
//        System.out.println(Arrays.toString(tablesFormSql.toArray()));

//        String result = appendTablePrefix(sql, "tb_");

//        String foreignKeysDDL = "CREATE TABLE Grades (\n" +
//                "    grade_id INT PRIMARY KEY,\n" +
//                "    student_id INT,\n" +
//                "    course_id INT,\n" +
//                "    score FLOAT COMMENT '成绩',\n" +
//                "    FOREIGN KEY (student_id) REFERENCES Students(student_id),\n" +
//                "    FOREIGN KEY (course_id) REFERENCES Courses(course_id)\n" +
//                ");";
//
//        String result = removeForeignKeys(foreignKeysDDL);

//        String result = makeColumnsAllowNullAndUnderlineNamed(sql);
        System.out.println(result);
    }


    public static String getSimilarilyDDL(TableInfo tableInfo) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableInfo.getTableName()).append(" (");
        List<ColumnInfo> columnInfoList = tableInfo.getColumnInfoList();
        IdInfo idInfo = null;
        for (ColumnInfo columnInfo : columnInfoList) {
            String column = StringUtil.firstCharToLowerCase(StringUtil.underlineToCamel(columnInfo.getColumn()));
            sql.append(column).append(" ").append(getTypeAndLength(columnInfo.getPropertyType()));
            if (columnInfo instanceof IdInfo) {
                sql.append(" unsigned NOT NULL COMMENT '").append(columnInfo.getComment()).append("',\n");
                idInfo = (IdInfo) columnInfo;
            } else {
                sql.append(" DEFAULT NULL COMMENT '").append(columnInfo.getComment()).append("',\n");
            }
        }
        if (idInfo != null) {
            sql.append("PRIMARY KEY (").append(idInfo.getColumn()).append(")");
        } else {
            sql.deleteCharAt(sql.length() - 1);
        }

        return sql.append(") ENGINE=InnoDB COMMENT +").append(tableInfo.getComment()).append(";").toString();
    }

    private static String getTypeAndLength(Class<?> propertyType) {
        if (propertyType == BigInteger.class) {
            return "bigint(20)";
        } else if (propertyType == String.class) {
            return "text";
        } else if (Date.class == propertyType) {
            return "datetime";
        }
        return "varchar(32)";
    }


}
