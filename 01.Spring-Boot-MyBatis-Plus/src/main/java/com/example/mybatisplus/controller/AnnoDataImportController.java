package com.example.mybatisplus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.mybatisplus.dao.KgapAnnoDataVersionMapper;
import com.example.mybatisplus.dao.KgapAnnoEntLabelMapper;
import com.example.mybatisplus.dao.KgapAnnoRelLabelMapper;
import com.example.mybatisplus.dao.KgapAnnoTaskLabelsMapper;
import com.example.mybatisplus.entity.KgapAnnoDataVersion;
import com.example.mybatisplus.entity.KgapAnnoEntLabel;
import com.example.mybatisplus.entity.KgapAnnoRelLabel;
import com.example.mybatisplus.entity.KgapAnnoTaskLabels;
import com.example.mybatisplus.utils.CustomFilePart;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.HttpClient;

@RestController
public class AnnoDataImportController {

//    public static String[] staff = {"anno3", "anno4"};
    public static String[] staff = {"drmp", "dcap3w_user3"};
    public static Long[] staffDocOrder = {0L, 0L};
    @Resource
    KgapAnnoTaskLabelsMapper taskLabelsMapper;
    @Resource
    KgapAnnoDataVersionMapper kgapAnnoDataVersionMapper;
    @Resource
    KgapAnnoEntLabelMapper entLabelMapper;
    @Resource
    KgapAnnoRelLabelMapper relLabelMapper;
//    private static Long taskId = 43L;
//    private static int dataSetInfoId = 112602;
//    private final static int stride=100;

    private static Long taskId = 25L;
    private static int dataSetInfoId = 112600;
    private final static int stride=100;
    private final static String schemaPath="schemas_baidu.json";
    private final static String dataPath="dev_data_baidu.json";

    //导入数据
    public void importAnnoData() throws URISyntaxException, IOException {
        String path = this.getClass().getClassLoader().getResource(dataPath).toURI().getPath();
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Map<String, Long> indexMap = new HashMap<>();
        indexMap.put("id", 1L);
        bufferedReader.lines()
                .forEach(
                        line -> {
                            Map<String, Object> map = JSONObject.parseObject(line, Map.class);
                            List<Object> spo_list = JSONObject.parseObject(JSONObject.toJSONString(map.get("spo_list")), List.class);
                            String text = JSONObject.parseObject(JSONObject.toJSONString(map.get("text")), String.class);
                            List<Map<String, Object>> relList = new ArrayList<>();
                            List<Map<String, Object>> entList = new ArrayList<>();
                            indexMap.put("index", 1L);
                            spo_list.stream()
                                    .map(item -> JSONObject.toJSONString(item))
                                    .map(item -> (Map<String, String>) JSONObject.parseObject(item, Map.class))
                                    .forEach(
                                            mapStr -> {
                                                /**
                                                 * {
                                                 *         "predicate": "出生地",
                                                 *         "object_type": "地点",
                                                 *         "subject_type": "人物",
                                                 *         "object": "圣地亚哥",
                                                 *         "subject": "查尔斯·阿兰基斯"
                                                 *     },
                                                 */
                                                String predicate = mapStr.get("predicate");
                                                String subject = mapStr.get("subject");
                                                String subject_type = mapStr.get("subject_type");
                                                String object = mapStr.get("object");
                                                String object_type = mapStr.get("object_type");
                                                //主题id
                                                KgapAnnoTaskLabels sujectLabel = this.taskLabelsMapper.selectOne(Wrappers.<KgapAnnoTaskLabels>query()
                                                        .lambda()
                                                        .eq(KgapAnnoTaskLabels::getType, 1)
                                                        .eq(KgapAnnoTaskLabels::getName, subject_type));
                                                Long subjectId = sujectLabel.getId();
                                                //宾语id
                                                KgapAnnoTaskLabels objectLabel = this.taskLabelsMapper.selectOne(Wrappers.<KgapAnnoTaskLabels>query()
                                                        .lambda()
                                                        .eq(KgapAnnoTaskLabels::getType, 1)
                                                        .eq(KgapAnnoTaskLabels::getName, object_type));
                                                Long objectId = objectLabel.getId();
                                                //谓语
                                                KgapAnnoTaskLabels predicateObject = this.taskLabelsMapper.selectOne(Wrappers.<KgapAnnoTaskLabels>query()
                                                        .lambda().eq(KgapAnnoTaskLabels::getType, 2)
                                                        .eq(KgapAnnoTaskLabels::getName, predicate)
                                                        .eq(KgapAnnoTaskLabels::getFromLabel, subjectId)
                                                        .eq(KgapAnnoTaskLabels::getEndLabel, objectId)
                                                );
                                                //关系标注版本信息
                                                Long id = indexMap.get("id");
                                                Long startId = ++id;
                                                Long endId = ++id;
                                                indexMap.put("id", id);
                                                Map<String, Object> relMap = new HashMap<>();
                                                relMap.put("label_id", predicateObject.getId());
                                                relMap.put("label_name", predicateObject.getName());
                                                relMap.put("label_enName", predicateObject.getEnname());
                                                relMap.put("from_anno_id", startId);
                                                relMap.put("from_anno_name", subject_type);
                                                relMap.put("from_text", subject);
                                                relMap.put("end_anno_Id", endId);
                                                relMap.put("end_anno_name", object_type);
                                                relMap.put("end_text", object);
                                                relList.add(relMap);

                                                Long index = indexMap.get("index");
                                                //实体标注版本信息（主语）
                                                Map<String, Object> entMap = new HashMap<>();
                                                entMap.put("id", startId);
                                                entMap.put("label_id", sujectLabel.getId());
                                                entMap.put("label_name", sujectLabel.getName());
                                                entMap.put("label_enName", sujectLabel.getEnname());
                                                entMap.put("text", subject);
                                                entMap.put("startIndex", index);
                                                index += 2;
                                                entMap.put("endIndex", index);
                                                entList.add(entMap);
                                                //实体标注版本信息（宾语）
                                                index += 5;
                                                Map<String, Object> entMap1 = new HashMap<>();
                                                entMap1.put("id", endId);
                                                entMap1.put("label_id", objectLabel.getId());
                                                entMap1.put("label_name", objectLabel.getName());
                                                entMap1.put("label_enName", objectLabel.getEnname());
                                                entMap1.put("text", object);
                                                entMap1.put("startIndex", index);
                                                index += 2;
                                                entMap1.put("endIndex", index);
                                                entList.add(entMap1);
                                                //自增index--10
                                                index += 10;
                                                indexMap.put("index", index);
                                            }
                                    );

                            KgapAnnoDataVersion kgapAnnoDataVersion = new KgapAnnoDataVersion();
                            kgapAnnoDataVersion.setRelAnnos(JSON.toJSONString(relList));
                            kgapAnnoDataVersion.setEntAnnos(JSON.toJSONString(entList));
                            kgapAnnoDataVersion.setVersionId(-1L);
                            kgapAnnoDataVersion.setTaskId(taskId);
                            kgapAnnoDataVersion.setDocContent(text);
                            this.kgapAnnoDataVersionMapper.insert(kgapAnnoDataVersion);
                        }
                );
    }

    /**
     * 导入数据到ent和rel表中
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public void importAnnoDataToEntAndRel() throws Exception, IOException {
        String path = this.getClass().getClassLoader().getResource(dataPath).toURI().getPath();
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CompletionService<String> completionService =
                new ExecutorCompletionService<>(
                        executorService);
        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        int size = lines.size();
        for (int step = 0; step < size; step+=stride) {
            List<String> blockLines;
            if(size-step>=stride){
                blockLines=lines.subList(step,step+stride);
            }else{
                blockLines=lines.subList(step,size);
            }
            final int finalStep=step;
            completionService.submit(() -> generateLabelDataFromLine(finalStep,blockLines));
        }
    }

    public String generateLabelDataFromLine(int step,List<String> lines){
        for (String line : lines) {
            Map<String, Object> map = JSONObject.parseObject(line, Map.class);
            List<Object> spo_list = JSONObject.parseObject(JSONObject.toJSONString(map.get("spo_list")), List.class);
            String text = String.valueOf(map.get("text"));

            long l = System.currentTimeMillis();
            String tmpPath = "D:\\tmp\\" + l + step+".txt";
            File f = new File(tmpPath);//新建一个文件对象，如果不存在则创建一个该文件
            FileWriter fw;
            try {
                fw = new FileWriter(f);
                fw.write(text);//将字符串写入到指定的路径下的文件中
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String docId = UUID.randomUUID().toString().replace("-", "");
            try {
                uploadFile(f, docId);
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int randomNumber = new Random().nextInt(2);
            Long docOrder;
            synchronized (AnnoDataImportController.staffDocOrder){
                docOrder= ++AnnoDataImportController.staffDocOrder[randomNumber];
            }
            String staff = AnnoDataImportController.staff[randomNumber];
            spo_list.stream()
                    .map(item -> JSONObject.toJSONString(item))
                    .map(item -> (Map<String, String>) JSONObject.parseObject(item, Map.class))
                    .forEach(
                            mapStr -> {
                                /**
                                 * {
                                 *         "predicate": "出生地",
                                 *         "object_type": "地点",
                                 *         "subject_type": "人物",
                                 *         "object": "圣地亚哥",
                                 *         "subject": "查尔斯·阿兰基斯"
                                 *     },
                                 */
                                String predicate = mapStr.get("predicate");
                                String subject = mapStr.get("subject");
                                String subject_type = mapStr.get("subject_type");
                                String object = mapStr.get("object");
                                String object_type = mapStr.get("object_type");
                                //主题id
                                KgapAnnoTaskLabels sujectLabel = this.taskLabelsMapper.selectOne(
                                        Wrappers.<KgapAnnoTaskLabels>query()
                                                .lambda()
                                                .eq(KgapAnnoTaskLabels::getType, 1)
                                                .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                                                .eq(KgapAnnoTaskLabels::getName, subject_type));
                                Long subjectId = sujectLabel.getId();
                                //宾语id
                                KgapAnnoTaskLabels objectLabel = this.taskLabelsMapper.selectOne(
                                        Wrappers.<KgapAnnoTaskLabels>query()
                                                .lambda()
                                                .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                                                .eq(KgapAnnoTaskLabels::getType, 1)
                                                .eq(KgapAnnoTaskLabels::getName, object_type));
                                if (objectLabel == null) {
                                    objectLabel = this.taskLabelsMapper.selectOne(
                                            Wrappers.<KgapAnnoTaskLabels>query()
                                                    .lambda()
                                                    .eq(KgapAnnoTaskLabels::getType, 1)
                                                    .eq(KgapAnnoTaskLabels::getName, object_type.toLowerCase()));
                                }
                                Long objectId = objectLabel.getId();
                                //谓语
                                KgapAnnoTaskLabels predicateObject = this.taskLabelsMapper.selectOne(
                                        Wrappers.<KgapAnnoTaskLabels>query()
                                                .lambda().eq(KgapAnnoTaskLabels::getType, 2)
                                                .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                                                .eq(KgapAnnoTaskLabels::getName, predicate)
                                                .eq(KgapAnnoTaskLabels::getFromLabel, subjectId)
                                                .eq(KgapAnnoTaskLabels::getEndLabel, objectId)
                                );
                                //实体标注版本信息（主语）
                                List<KgapAnnoEntLabel> subjectKgapAnnoEntLabels = this.entLabelMapper
                                        .selectList(Wrappers.<KgapAnnoEntLabel>query().lambda()
                                                .select(KgapAnnoEntLabel::getId)
                                                .eq(KgapAnnoEntLabel::getTaskId, taskId)
                                                .eq(KgapAnnoEntLabel::getDocId, docId)
                                        .eq(KgapAnnoEntLabel::getText, subject));
                                Long startId;
                                if(subjectKgapAnnoEntLabels.size()==0){
                                    KgapAnnoEntLabel kgapAnnoEntLabel = new KgapAnnoEntLabel();
                                    kgapAnnoEntLabel.setLabelId(sujectLabel.getId());
                                    kgapAnnoEntLabel.setText(subject);
                                    kgapAnnoEntLabel.setLabelName(sujectLabel.getName());
                                    kgapAnnoEntLabel.setDocId(docId);
                                    kgapAnnoEntLabel.setTaskId(taskId);
                                    kgapAnnoEntLabel.setDocOrder(docOrder);
                                    kgapAnnoEntLabel.setAnnoUser(staff);
                                    kgapAnnoEntLabel.setStartindex((long) text.indexOf(subject));
                                    kgapAnnoEntLabel.setEndindex((long) text.indexOf(subject) + subject.length());
                                    this.entLabelMapper.insert(kgapAnnoEntLabel);
                                    startId = kgapAnnoEntLabel.getId();
                                }else{
                                    startId=subjectKgapAnnoEntLabels.get(0).getId();
                                }

                                //实体标注版本信息（宾语）
                                List<KgapAnnoEntLabel> objectKgapAnnoEntLabels = this.entLabelMapper.selectList(
                                        Wrappers.<KgapAnnoEntLabel>query().lambda()
                                                .select(KgapAnnoEntLabel::getId)
                                                .eq(KgapAnnoEntLabel::getTaskId, taskId)
                                                .eq(KgapAnnoEntLabel::getDocId, docId)
                                        .eq(KgapAnnoEntLabel::getText, object));
                                Long endId;
                                if(objectKgapAnnoEntLabels.size()==0){
                                    KgapAnnoEntLabel kgapAnnoEntLabel1 = new KgapAnnoEntLabel();
                                    kgapAnnoEntLabel1.setLabelId(objectLabel.getId());
                                    kgapAnnoEntLabel1.setText(object);
                                    kgapAnnoEntLabel1.setLabelName(objectLabel.getName());
                                    kgapAnnoEntLabel1.setDocId(docId);
                                    kgapAnnoEntLabel1.setTaskId(taskId);
                                    kgapAnnoEntLabel1.setDocOrder(docOrder);
                                    kgapAnnoEntLabel1.setAnnoUser(staff);
                                    kgapAnnoEntLabel1.setStartindex((long) text.indexOf(object));
                                    kgapAnnoEntLabel1.setEndindex((long) text.indexOf(object) + object.length());
                                    this.entLabelMapper.insert(kgapAnnoEntLabel1);
                                    endId = kgapAnnoEntLabel1.getId();
                                }else{
                                    endId=objectKgapAnnoEntLabels.get(0).getId();
                                }
                                //关系标注信息
                                KgapAnnoRelLabel kgapAnnoRelLabel = new KgapAnnoRelLabel();
                                kgapAnnoRelLabel.setDocId(docId);
                                kgapAnnoRelLabel.setLabelId(predicateObject.getId());
                                kgapAnnoRelLabel.setLabelName(predicateObject.getName());
                                kgapAnnoRelLabel.setFromAnnoId(Long.valueOf(startId));
                                kgapAnnoRelLabel.setEndAnnoId(Long.valueOf(endId));
                                kgapAnnoRelLabel.setFromText(subject);
                                kgapAnnoRelLabel.setEndText(object);
                                kgapAnnoRelLabel.setTaskId(taskId);
                                kgapAnnoRelLabel.setAnnoUser(staff);
                                this.relLabelMapper.insert(kgapAnnoRelLabel);
                                //自增index--10
                            }
                    );
        }
        return "11";
    }
    //生成标注实体标签数据集
    @Transactional
    public void genereteTaskLabelsEntity() throws URISyntaxException, IOException {
        String path = this.getClass().getClassLoader().getResource(schemaPath).toURI().getPath();
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        bufferedReader.lines()
                .forEach(
                        line -> {
                            //数据格式{"object_type": "地点", "predicate": "祖籍", "subject_type": "人物"}
                            Map<String, String> map = JSONObject.parseObject(line, Map.class);
                            KgapAnnoTaskLabels kgapAnnoTaskLabels = new KgapAnnoTaskLabels();
                            String subject_type = map.get("subject_type");
                            List<KgapAnnoTaskLabels> subjectList = this.taskLabelsMapper.selectList(
                                    Wrappers.<KgapAnnoTaskLabels>query()
                                            .lambda()
                                            .eq(KgapAnnoTaskLabels::getName, subject_type)
                                            .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                            );
                            if (subjectList.size() == 0) {
                                kgapAnnoTaskLabels.setEnname(this.toHanYuPinyinString(subject_type));
                                kgapAnnoTaskLabels.setName(subject_type);
                                kgapAnnoTaskLabels.setType(1);
                                kgapAnnoTaskLabels.setTaskId(taskId);
                                this.taskLabelsMapper.insert(kgapAnnoTaskLabels);
                            }

                            String object_type = map.get("object_type");
                            List<KgapAnnoTaskLabels> objectList = this.taskLabelsMapper.selectList(
                                    Wrappers
                                            .<KgapAnnoTaskLabels>query()
                                            .lambda()
                                            .eq(KgapAnnoTaskLabels::getName, object_type)
                                            .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                            );
                            if (objectList.size() == 0) {
                                kgapAnnoTaskLabels.setEnname(this.toHanYuPinyinString(object_type));
                                kgapAnnoTaskLabels.setName(object_type);
                                kgapAnnoTaskLabels.setType(1);
                                kgapAnnoTaskLabels.setTaskId(taskId);
                                this.taskLabelsMapper.insert(kgapAnnoTaskLabels);
                            }
                        }
                );
    }

    //生成标注实体标签数据集
    @Transactional
    public void genereteTaskLabelsRelation() throws URISyntaxException, IOException {
        String path = this.getClass().getClassLoader().getResource(schemaPath).toURI().getPath();
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        bufferedReader.lines()
                .forEach(
                        line -> {
                            //数据格式{"object_type": "地点", "predicate": "祖籍", "subject_type": "人物"}
                            Map<String, String> map = JSONObject.parseObject(line, Map.class);
                            KgapAnnoTaskLabels kgapAnnoTaskLabels = new KgapAnnoTaskLabels();
                            String predicate = map.get("predicate");
                            String subject_type = map.get("subject_type");
                            String object_type = map.get("object_type");
                            System.out.println(subject_type);
                            Long subjectId = this.taskLabelsMapper.selectOne(Wrappers.<KgapAnnoTaskLabels>query()
                                    .lambda()
                                    .eq(KgapAnnoTaskLabels::getType, 1)
                                    .eq(KgapAnnoTaskLabels::getName, subject_type)
                                    .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                            )
                                    .getId();

                            Long objectId = this.taskLabelsMapper.selectOne(Wrappers.<KgapAnnoTaskLabels>query()
                                    .lambda()
                                    .eq(KgapAnnoTaskLabels::getType, 1)
                                    .eq(KgapAnnoTaskLabels::getName, object_type)
                                    .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                            )
                                    .getId();

                            List<KgapAnnoTaskLabels> predicateList = this.taskLabelsMapper.selectList(
                                    Wrappers.<KgapAnnoTaskLabels>query()
                                            .lambda()
                                            .eq(KgapAnnoTaskLabels::getType, 2)
                                            .eq(KgapAnnoTaskLabels::getTaskId, taskId)
                                            .eq(KgapAnnoTaskLabels::getName, predicate));

                            boolean present = predicateList.stream()
                                    .anyMatch(
                                            item -> item.getFromLabel().equals(subjectId) && item.getEndLabel().equals(objectId)
                                    );
                            if (!present) {
                                kgapAnnoTaskLabels.setEnname(this.toHanYuPinyinString(predicate));
                                kgapAnnoTaskLabels.setName(predicate);
                                kgapAnnoTaskLabels.setType(2);
                                kgapAnnoTaskLabels.setTaskId(taskId);
                                kgapAnnoTaskLabels.setFromLabel(subjectId);
                                kgapAnnoTaskLabels.setEndLabel(objectId);
                                this.taskLabelsMapper.insert(kgapAnnoTaskLabels);
                            }
                        }
                );
    }

    public String toHanYuPinyinString(String originalMessage) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        try {
            // I know this is deprecated but there's no viable alternative...
            String s = PinyinHelper.toHanYuPinyinString(originalMessage, format, "", false);
            return s;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
        }
        return "";
    }


    public static String uploadFile(File file, String docId) throws Exception {
        String url = "http://172.16.40.155:8080/fscrawler/_upload";
        Map<String, String> params = new HashMap<>();
        params.put("id", docId);
        params.put("tags", "{\"external\":{\"infoId\":" + dataSetInfoId + ",\"uploader\":\"超级管理员\",\"filesize\":0,\"uploadTime\":1600746179691}}");
        String body = "";
        PostMethod postMethod = new PostMethod(url);
        CustomFilePart fp = new CustomFilePart("file", file);
        List<Part> parts = new ArrayList<>();
        parts.add(fp);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            parts.add(new StringPart(entry.getKey(), entry.getValue(), StandardCharsets.UTF_8.name()));
        }
        MultipartRequestEntity mre = new MultipartRequestEntity(parts.toArray(new Part[0]), postMethod.getParams());
        postMethod.setRequestEntity(mre);
        HttpClient client = new HttpClient();
        // 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
        client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
        int status = client.executeMethod(postMethod);
        if (status == 200) {
            InputStream inputStream = postMethod.getResponseBodyAsStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
            body = stringBuffer.toString();
        } else {
            body = "fail";
        }
        return body;
    }
}
