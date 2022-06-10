package com.sf.batch;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.sf.bean.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 批量Job
 **/
@Slf4j
@Component
public class BatchJobDemo {

    /**
     * Job构建工厂，用于构建Job
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * Step构建工厂，用于构建Step
     */
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    /**
     * 自定义的简单Job监听器
     */
    private final JobListener jobListener;

    private final String filePath = "D:/git/docker_springboot/src/main/resources/file/user.csv";

    @Autowired
    public BatchJobDemo(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory,
                        JobListener jobListener) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobListener = jobListener;
    }

    /**
     * 一个最基础的Job通常由一个或者多个Step组成
     */
    @Bean
    public Job multiJob() {
        return jobBuilderFactory.get("multiJob").
                incrementer(new RunIdIncrementer()).
//                start(sourceDbStep()).
                start(sourceCsvStep()).
//                next(sourceCsvStep()).
        listener(jobListener).
                build();
    }

    //---------------------------------------------- step1 ------------------------------------------------

    /**
     * 一个简单基础的Step主要分为三个部分
     * ItemReader : 用于读取数据
     * ItemProcessor : 用于处理数据
     * ItemWriter : 用于写数据
     */
    @Bean
    private Step sourceDbStep() {
        return stepBuilderFactory.get("sourceDbStep").
                // <输入对象, 输出对象>  chunk通俗的讲类似于SQL的commit; 这里表示处理(processor)100条后写入(writer)一次
                <UserBean, UserBean>chunk(100).
                // 容错：捕捉到异常就重试,重试100次还是异常,JOB就停止并标志失败
                faultTolerant().retryLimit(3).retry(Exception.class).skipLimit(100).skip(Exception.class).
                // 指定ItemReader对象
                reader(sourceDbReader()).
                // 指定ItemProcessor对象
                processor(DataProcessor()).
                // 指定ItemWriter对象
                writer(sourceDbWriter()).
                build();
    }

    /**
     * 读取DB数据
     */
    private ItemReader<? extends UserBean> sourceDbReader() {
        // 读取数据-Mybatis
        MyBatisCursorItemReader<UserBean> reader = new MyBatisCursorItemReaderBuilder<UserBean>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.sf.mapper.UserMapper.queryUserList")
                .parameterValues(new HashMap<String, Object>() {{
                    put("sex", "男");
                }})
                // 所有ItemReader和ItemWriter实现都会在ExecutionContext提交之前将其当前状态存储在其中
                // 如果不希望这样做,可以设置setSaveState(false)
                .saveState(true)
                .build();

        /*// 读取数据-JPA
        JpaPagingItemReader<UserBean> reader = new JpaPagingItemReader<>();
        try {
            // 这里选择JPA方式读取数据
            JpaNativeQueryProvider<UserBean> queryProvider = new JpaNativeQueryProvider<>();
            // 一个简单的 native SQL
            queryProvider.setSqlQuery("select * from t_user");
            // 设置实体类
            queryProvider.setEntityClass(UserBean.class);
            queryProvider.afterPropertiesSet();
            reader.setEntityManagerFactory(emf);
            // 设置每页读取的记录数
            reader.setPageSize(3);
            // 设置数据提供者
            reader.setQueryProvider(queryProvider);
            reader.afterPropertiesSet();
            reader.setSaveState(true);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return reader;
    }

    /**
     * 处理数据
     */
    private ItemProcessor<UserBean, UserBean> DataProcessor() {
        //return userBean -> {todo...}
        log.info("processor data: 数据校验中...");
        //校验器
        UserValidateItemProcessor userValidateItemProcessor = new UserValidateItemProcessor();
        userValidateItemProcessor.setValidator(new BeanValidator<>());
        return userValidateItemProcessor;
    }

    /**
     * 写入数据
     */
    private ItemWriter<UserBean> sourceDbWriter() {
        CsvWriter writer = CsvUtil.getWriter(filePath, CharsetUtil.CHARSET_UTF_8);
        return list -> {
            writer.write(addCsvHeader(list));
            log.info("数据成功写入CSV文件!");
            writer.close();
        };
    }
    //数据转换
    private static List<Object> addCsvHeader(List<? extends UserBean> list) {
        //头部字段名称
        Field[] fields = UserBean.class.getDeclaredFields();
        String[] headers = Arrays.stream(fields).map(Field::getName).collect(Collectors.joining(",")).split(",");
        Arrays.sort(headers); //对listName进行排序，保证Arrays.binarySearch返回值不为负数
        //将headers添加到result中
        List<Object> result = new ArrayList<>();
        result.add(headers);
        for (UserBean user : list) {
            Field[] declaredFields = user.getClass().getDeclaredFields();
            Object[] values = new Object[declaredFields.length];
            Arrays.stream(declaredFields).forEach(f -> {
                f.setAccessible(true);
                //获取与headers中对应属性名的索引值
                int index = Arrays.binarySearch(headers, f.getName());
                //将属性值添加到listValue对应的索引位置下
                try {
                    values[index] = f.get(user);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            result.add(values);
        }
        return result;
    }

    //----------------------------------------------- step2 -----------------------------------------------

    @Bean
    @StepScope
    private Step sourceCsvStep() {
        return stepBuilderFactory.get("sourceCsvStep").
                // <输入对象, 输出对象>  chunk通俗的讲类似于SQL的commit; 这里表示处理(processor)100条后写入(writer)一次
                <UserBean, UserBean>chunk(100).
                // 捕捉到异常就重试3次每条, 失败数据达到100次, JOB就停止并标志失败
                faultTolerant().retryLimit(3).retry(Exception.class).skipLimit(100).skip(Exception.class).
                // 指定ItemReader对象
                reader(sourceCsvReader()).
                // 指定ItemProcessor对象
                processor(DataProcessor()).
                // 指定ItemWriter对象
                writer(sourceCsvWriter()).
                build();
    }

    private ItemReader<UserBean> sourceCsvReader() {
        //List<UserBean> list = CsvUtil.getReader().read(ResourceUtil.getUtf8Reader("user.csv"), UserBean.class);
        FlatFileItemReader<UserBean> reader = new FlatFileItemReaderBuilder<UserBean>()
                .name("sourceCsvReader")
                .resource(new FileSystemResource(filePath))
                // String -> Object
                .delimited()
                .names("age","createDate","id","name","phone","sex")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<UserBean>() {{
                    setTargetType(UserBean.class);
                }})
                /*.lineMapper(new DefaultLineMapper<UserBean>(){{
                    setLineTokenizer(new DelimitedLineTokenizer(){{
                        setNames(new String[]{"age","createDate","id","name","phone","sex"});
                    }});
                    setFieldSetMapper(new BeanWrapperFieldSetMapper<UserBean>() {{
                        setTargetType(UserBean.class);
                    }});
                }})*/
                .linesToSkip(1) //跳过标题行
                .build();
        return reader;
    }

    @Autowired
    private DataSource dataSource;

    private ItemWriter<UserBean> sourceCsvWriter() {
        /*MyBatisBatchItemWriter<UserBean> writer = new MyBatisBatchItemWriterBuilder<UserBean>()
                //.sqlSessionTemplate(new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH))
                .sqlSessionFactory(sqlSessionFactory)
                .assertUpdates(false)
                .statementId("com.sf.mapper.UserMapper.insertUser")
                .build();*/
        // JDBC写入库
        JdbcBatchItemWriter<UserBean> writer = new JdbcBatchItemWriterBuilder<UserBean>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource) //dataSource注入获取
                .sql("insert into t_user(name, sex, phone) values(:name, :sex, :phone)")
                .build();
        return writer;
    }
}