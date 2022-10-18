package com.yuri.empire.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.yuri.empire.database.DataRepository;
import com.yuri.empire.models.BaseModel;
import com.yuri.empire.models.enumz.JobType;
import com.yuri.empire.models.enumz.RarityType;
import com.yuri.empire.models.game.SHeroInfo;
import com.yuri.empire.models.master.MScenarioContent;
import com.yuri.empire.models.master.MSkillAnimation;
import com.yuri.empire.utils.CmdUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController()
@RequestMapping("/import")
@CrossOrigin(origins = "*")
public class ImportController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<BaseModel> models;
    private List<MSkillAnimation> skillAModels;
    private Map<String, BaseModel> modelMapper;

    public ImportController(List<BaseModel> baseModels) {
        this.models = baseModels;
        this.modelMapper = new HashMap<>();
        this.skillAModels = new ArrayList<>();
        for (BaseModel model : baseModels) {
            modelMapper.put(model.getClass().getSimpleName(), model);
            if (model instanceof MSkillAnimation) {
                skillAModels.add((MSkillAnimation) model);
            }
        }
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @SneakyThrows
    @GetMapping("/template")
    public ResponseEntity<Object> getTemplate() {
        CmdUtils.createFolder("template", true);
        for (BaseModel model : models) {
            log.info("Model: {}", model.getClass().getSimpleName());
            List<Field> declaredFields = Arrays.stream(model.getClass().getSuperclass().getDeclaredFields()).collect(Collectors.toList());
            declaredFields.addAll(Arrays.stream(model.getClass().getDeclaredFields()).collect(Collectors.toList()));

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("data");
            for (int i = 0; i < 1; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < declaredFields.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(declaredFields.get(j).getName());
                }
            }
            exportFile("template/" + model.getClass().getSimpleName() + ".xlsx", workbook);
        }
        String sourcePath = "template";
        String sinkPath = "template/" + "master_data_template.zip";
        compress(sourcePath, sinkPath);
        File file = new File(sinkPath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=master_data_template.zip");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @SneakyThrows
    @GetMapping("/dataInit")
    public ResponseEntity<Object> createDataSample(int size) {
        CmdUtils.createFolder("initialize", true);
        for (BaseModel model : models) {
            log.info("Model: {}", model.getClass().getSimpleName());
            List<Field> declaredFields = Arrays.stream(model.getClass().getSuperclass().getDeclaredFields()).collect(Collectors.toList());
            declaredFields.addAll(Arrays.stream(model.getClass().getDeclaredFields()).collect(Collectors.toList()));

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("data");
            for (int i = 0; i < size; i++) {
                Row row = sheet.createRow(i);
                if (i == 0) {
                    for (int j = 0; j < declaredFields.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(declaredFields.get(j).getName());
                    }
                } else {
                    for (int j = 0; j < declaredFields.size(); j++) {
                        Field field = declaredFields.get(j);
                        Cell cell = row.createCell(j);
                        if (field.getType() instanceof Class && field.getType().isEnum()) {
                            int length = (((Class) field.getGenericType()).getEnumConstants()).length;
                            cell.setCellValue(RandomUtils.nextInt(0, length));
                        } else if (field.getType().isAssignableFrom(boolean.class)) {
                            cell.setCellValue(RandomUtils.nextInt(1, 100) % 2 == 0 ? true : false);
                        } else if (field.getType().isAssignableFrom(int.class)) {
                            cell.setCellValue(RandomUtils.nextInt(1, 100));
                        } else if (field.getType().isAssignableFrom(float.class)) {
                            cell.setCellValue(RandomUtils.nextInt(1, 100) * 1.0f);
                        } else if (field.getType().isAssignableFrom(String.class)) {
                            cell.setCellValue(RandomStringUtils.randomAlphabetic(7));
                        } else if (field.getType().isAssignableFrom(List.class)) {
                            ParameterizedType typeArguments = (ParameterizedType) field.getGenericType();
                            Class<?> argClazz = (Class<?>) typeArguments.getActualTypeArguments()[0];
                            if (argClazz.isAssignableFrom(String.class)) {
                                StringBuilder tmp = new StringBuilder();
                                String s = "";
                                for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                                    tmp.append(s).append("\"").append(RandomStringUtils.randomAlphabetic(5)).append("\"");
                                    s = ",";
                                }
                                cell.setCellValue(tmp.toString());
                            } else if (argClazz.isAssignableFrom(Integer.class)) {
                                StringBuilder tmp = new StringBuilder();
                                String s = "";
                                for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                                    tmp.append(s).append(RandomUtils.nextInt(1, 10));
                                    s = ",";
                                }
                                cell.setCellValue(tmp.toString());
                            } else if (argClazz.isAssignableFrom(Float.class)) {
                                StringBuilder tmp = new StringBuilder();
                                String s = "";
                                for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                                    tmp.append(s).append(RandomUtils.nextInt(1, 10) * 1.0);
                                    s = ",";
                                }
                                cell.setCellValue(tmp.toString());
                            } else if (argClazz.isAssignableFrom(MSkillAnimation.class)) {
                                ArrayNode arrayNode = objectMapper.createArrayNode();
                                Collections.shuffle(skillAModels);
                                int tmp = RandomUtils.nextInt(2, 4);
                                for (int k = 0; k < tmp; k++) {
                                    arrayNode.add(createSampleData(skillAModels.get(k)));
                                }
                                cell.setCellValue(arrayNode.toString());
                            }
                        }
                    }
                }
            }
            exportFile("initialize/" + model.getClass().getSimpleName() + ".xlsx", workbook);
        }

        String sourcePath = "initialize";
        String sinkPath = "initialize/" + "master_data_init.zip";
        compress(sourcePath, sinkPath);
        File file = new File(sinkPath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=master_data_init.zip");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private ObjectNode createSampleData(BaseModel model) {
        List<Field> declaredFields = Arrays.stream(model.getClass().getSuperclass().getDeclaredFields()).collect(Collectors.toList());
        declaredFields.addAll(Arrays.stream(model.getClass().getDeclaredFields()).collect(Collectors.toList()));
        ObjectNode object = objectMapper.createObjectNode();
        for (int j = 0; j < declaredFields.size(); j++) {
            Field field = declaredFields.get(j);
            if (field.getType() instanceof Class && field.getType().isEnum()) {
                int length = (((Class) field.getGenericType()).getEnumConstants()).length;
                object.put(field.getName(), RandomUtils.nextInt(0, length));
            } else if (field.getType().isAssignableFrom(boolean.class)) {
                object.put(field.getName(), RandomUtils.nextInt(1, 100) % 2 == 0 ? true : false);
            } else if (field.getType().isAssignableFrom(int.class)) {
                object.put(field.getName(), RandomUtils.nextInt(1, 100));
            } else if (field.getType().isAssignableFrom(float.class)) {
                object.put(field.getName(), RandomUtils.nextInt(1, 100) * 1.0f);
            } else if (field.getType().isAssignableFrom(String.class)) {
                object.put(field.getName(), RandomStringUtils.randomAlphabetic(7));
            } else if (field.getType().isAssignableFrom(List.class)) {
                ParameterizedType typeArguments = (ParameterizedType) field.getGenericType();
                Class<?> argClazz = (Class<?>) typeArguments.getActualTypeArguments()[0];
                if (argClazz.isAssignableFrom(String.class)) {
                    StringBuilder tmp = new StringBuilder();
                    String s = "";
                    for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                        tmp.append(s).append("\"").append(RandomStringUtils.randomAlphabetic(5)).append("\"");
                        s = ",";
                    }
                    object.put(field.getName(), tmp.toString());
                } else if (argClazz.isAssignableFrom(Integer.class)) {
                    StringBuilder tmp = new StringBuilder();
                    String s = "";
                    for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                        tmp.append(s).append(RandomUtils.nextInt(1, 10));
                        s = ",";
                    }
                    object.put(field.getName(), tmp.toString());
                } else if (argClazz.isAssignableFrom(Float.class)) {
                    StringBuilder tmp = new StringBuilder();
                    String s = "";
                    for (int k = 0; k < RandomUtils.nextInt(5, 10); k++) {
                        tmp.append(s).append(RandomUtils.nextInt(1, 10) * 1.0);
                        s = ",";
                    }
                    object.put(field.getName(), tmp.toString());
                } else if (argClazz.isAssignableFrom(MSkillAnimation.class)) {
                    ArrayNode arrayNode = objectMapper.createArrayNode();
                    Collections.shuffle(skillAModels);
                    int tmp = RandomUtils.nextInt(2, 4);
                    for (int k = 0; k < tmp; k++) {
                        arrayNode.add(createSampleData(skillAModels.get(k)));
                    }
                    object.put(field.getName(), arrayNode.textValue());
                }
            }
        }
        return object;
    }

    private void compress(String source, String sink) throws IOException {
        File dir = new File(source);
        List<String> srcFiles = Arrays.stream(dir.listFiles())
                .filter(file -> file.getName().endsWith("xlsx"))
                .map(File::getPath).collect(Collectors.toList());
        FileOutputStream fos = new FileOutputStream(sink, false);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }

    @SneakyThrows
    @PostMapping("/masterData")
    public String importMasterData(List<MultipartFile> files, boolean isOverride) {
        String modelName = "";
        String fieldName = "";
        String cellValue = "";
        String cellType = "";
        String sheetName = "";
        int rowNum = 0;
        int colNum = 0;

        try {
            log.info("importMasterData: {}", files.size());
            DBCollection mDataHash = DataRepository.masterDatabase().getCollection("MDataHash");
            for (MultipartFile file : files) {
                modelName = file.getOriginalFilename().replace(".xlsx", "");
                log.info("Import file: {}", modelName);
                BaseModel model = modelMapper.get(modelName);
                List<Field> declaredFields = Arrays.stream(model.getClass().getSuperclass().getDeclaredFields()).collect(Collectors.toList());
                declaredFields.addAll(Arrays.stream(model.getClass().getDeclaredFields()).collect(Collectors.toList()));

                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                int numberOfSheets = workbook.getNumberOfSheets();
                ArrayNode arrayNodes = objectMapper.createArrayNode();

                for (int i = 0; i < numberOfSheets; i++) {
                    XSSFSheet sheet = workbook.getSheetAt(i);
                    sheetName = sheet.getSheetName();
                    Iterator<Row> rows = sheet.iterator();
                    boolean isTitle = true;
                    while (rows.hasNext()) {
                        Row row = rows.next();
                        rowNum = row.getRowNum();
                        if (isTitle) {
                            isTitle = false;
                            continue;
                        }

                        if (row.getCell(0) == null || StringUtils.isEmpty(String.valueOf(row.getCell(0)))) {
                            break;
                        }
                        ObjectNode obj = objectMapper.createObjectNode();
                        for (int j = 0; j < declaredFields.size(); j++) {
                            Cell cell = row.getCell(j);
                            Field field = declaredFields.get(j);
                            colNum = cell.getColumnIndex();
                            fieldName = field.getName();
                            cellType = cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? "NUMERIC"
                                    : cell.getCellType() == Cell.CELL_TYPE_STRING ? "STRING"
                                    : cell.getCellType() == Cell.CELL_TYPE_FORMULA ? "FORMULA"
                                    : cell.getCellType() == Cell.CELL_TYPE_BLANK ? "BLANK"
                                    : cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ? "BOOLEAN"
                                    : cell.getCellType() == Cell.CELL_TYPE_ERROR ? "ERROR"
                                    : "UNKNOWN";

                            cellValue = String.valueOf(
                                cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell.getNumericCellValue()
                                : cell.getCellType() == Cell.CELL_TYPE_STRING ? cell.getStringCellValue()
                                : cell.getCellType() == Cell.CELL_TYPE_FORMULA ? cell.getCellFormula()
                                : cell.getCellType() == Cell.CELL_TYPE_BLANK ? ""
                                : cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ? cell.getBooleanCellValue()
                                : cell.getCellType() == Cell.CELL_TYPE_ERROR ? cell.getErrorCellValue()
                                : "unsupport cell type " + cellType
                            );

                            if (field.getType() instanceof Class && field.getType().isEnum()) {
                                obj.put(field.getName(), (int) cell.getNumericCellValue());
                            } else if (field.getType().isAssignableFrom(int.class)) {
                                obj.put(field.getName(), (int) cell.getNumericCellValue());
                            } else if (field.getType().isAssignableFrom(float.class)) {
                                obj.put(field.getName(), (float) cell.getNumericCellValue());
                            } else if (field.getType().isAssignableFrom(boolean.class)) {
                                obj.put(field.getName(), cell.getBooleanCellValue());
                            } else if (field.getType().isAssignableFrom(String.class)) {
                                obj.put(field.getName(), cell.getStringCellValue());
                            } else if (field.getType().isAssignableFrom(List.class)) {
                                ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
                                Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
                                if (integerListClass.isAssignableFrom(String.class)) {
                                    String value = cell.getStringCellValue();
                                    String[] split = value.split("\"");
                                    ArrayNode array = objectMapper.createArrayNode();
                                    for (String s : split) {
                                        if (StringUtils.isEmpty(s) || s.equals(",")) {
                                            continue;
                                        }
                                        array.add(s);
                                    }
                                    obj.set(field.getName(), array);
                                } else if (integerListClass.isAssignableFrom(Integer.class)) {
                                    String value = cell.getStringCellValue();
                                    String[] split = value.split(",");
                                    ArrayNode array = objectMapper.createArrayNode();
                                    for (String s : split) {
                                        array.add(Integer.parseInt(s));
                                    }
                                    obj.set(field.getName(), array);
                                } else if (integerListClass.isAssignableFrom(Float.class)) {
                                    String value = cell.getStringCellValue();
                                    String[] split = value.split(",");
                                    ArrayNode array = objectMapper.createArrayNode();
                                    for (String s : split) {
                                        array.add(NumberUtils.toFloat(s, 1.0f));
                                    }
                                    obj.set(field.getName(), array);
                                } else if (integerListClass.isAssignableFrom(MSkillAnimation.class)) {
                                    String value = cell.getStringCellValue();
                                    obj.set(field.getName(), objectMapper.readValue(value, ArrayNode.class));
                                } else if (integerListClass.isAssignableFrom(MScenarioContent.class)) {
                                    String value = cell.getStringCellValue();
                                    obj.set(field.getName(), objectMapper.readValue(value, ArrayNode.class));
                                }

                            }
                        }
                        arrayNodes.add(obj);
                    }

                }

                DBCollection collection = DataRepository.masterDatabase().getCollection(modelName);
                if (isOverride) {
                    BasicDBObject doc = new BasicDBObject();
                    collection.remove(doc);
                }

                for (JsonNode node : arrayNodes) {
                    BasicDBObject doc = new BasicDBObject();
                    HashMap<String, Object> keyValuePairs = new ObjectMapper().readValue(node.toPrettyString(), HashMap.class);
                    doc.putAll(keyValuePairs);
                    collection.insert(doc);
                }

                byte[] bytesOfMessage = arrayNodes.toString().getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(bytesOfMessage);
                BigInteger bigInt = new BigInteger(1, digest);
                String hashtext = bigInt.toString(16);
                while (hashtext.length() < 32) {
                    hashtext = "0" + hashtext;
                }
                DBObject whereClause = new BasicDBObject("mModel", modelName);
                BasicDBObject value = new BasicDBObject();
                value.put("mModel", modelName);
                value.put("md5", hashtext);
                WriteResult result = mDataHash.update(whereClause, value);
                if (!result.isUpdateOfExisting()) {
                    mDataHash.insert(value);
                }
                log.info("{} data: {}", model.getClass().getSimpleName(), arrayNodes);
            }
        } catch (Exception e) {
            String response = String.format("Import failed because:" +
                    "\n model: %s\n fieldName: %s\n cellValue: %s\n cellType: %s\n sheet: %s\n rowNum: %s\n colNum: %s\n error: %s",
                modelName, fieldName, cellValue, cellType, sheetName, rowNum, colNum, e.getMessage());
            log.error(response);
            return response;
        }
        return "Done";
    }

    @SneakyThrows
    @PostMapping("/generatePool")
    public String generatePool(List<MultipartFile> files) {
        log.info("generatePool: {}", files.size());
        CmdUtils.createFolder("poolHero", false);
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                String poolId = sheet.getSheetName();
                int poolSize = (int) sheet.getRow(9).getCell(1).getNumericCellValue();
                log.info("PoolSize: {}", poolSize);
                boolean isMinion = sheet.getRow(10).getCell(1).getBooleanCellValue();


                XSSFRow rarityRow = sheet.getRow(0);
                Iterator<Cell> cell = rarityRow.iterator();
                List<Pair<RarityType, Double>> rarityConfig = new ArrayList<>();

                while (cell.hasNext()) {
                    Cell rarity = cell.next();
                    if (rarity == null || Strings.isBlank(rarity.getStringCellValue())) {
                        break;
                    } else {
                        RarityType type = RarityType.valueOf(rarity.getStringCellValue().toUpperCase(Locale.ROOT));
                        rarityConfig.add(Pair.of(type, 0.0));
                    }
                }

                XSSFRow rarityRateRow = sheet.getRow(1);
                for (int j = 0; j < rarityConfig.size(); j++) {
                    Pair<RarityType, Double> rarityType = rarityConfig.get(j);
                    rarityConfig.set(j, Pair.of(rarityType.getLeft(), rarityRateRow.getCell(j).getNumericCellValue()));
                }

                XSSFRow jobRow = sheet.getRow(3);
                Iterator<Cell> jobCell = jobRow.iterator();
                List<Pair<JobType, Double>> jobConfig = new ArrayList<>();

                while (jobCell.hasNext()) {
                    Cell job = jobCell.next();
                    if (job == null || Strings.isBlank(job.getStringCellValue())) {
                        break;
                    } else {
                        JobType type = JobType.valueOf(job.getStringCellValue().toUpperCase(Locale.ROOT));
                        jobConfig.add(Pair.of(type, 0.0));
                    }
                }

                XSSFRow jobRateRow = sheet.getRow(4);
                for (int j = 0; j < jobConfig.size(); j++) {
                    Pair<JobType, Double> jobType = jobConfig.get(j);
                    jobConfig.set(j, Pair.of(jobType.getLeft(), jobRateRow.getCell(j).getNumericCellValue()));
                }

                XSSFRow kingDomRow = sheet.getRow(6);
                Iterator<Cell> kingDomCell = kingDomRow.iterator();
                List<Pair<String, Integer>> kingDomConfig = new ArrayList<>();

                while (kingDomCell.hasNext()) {
                    Cell cel = kingDomCell.next();
                    if (cel == null || Strings.isBlank(cel.getStringCellValue())) {
                        break;
                    } else {
                        kingDomConfig.add(Pair.of(cel.getStringCellValue(), 0));
                    }
                }

                XSSFRow kingDomRateRow = sheet.getRow(7);
                for (int j = 0; j < kingDomConfig.size(); j++) {
                    Pair<String, Integer> jobType = kingDomConfig.get(j);
                    int amount = (int) kingDomRateRow.getCell(j).getNumericCellValue();
                    kingDomConfig.set(j, Pair.of(jobType.getLeft(), amount));
                }

                log.info(rarityConfig.toString());
                log.info(jobConfig.toString());
                log.info(kingDomConfig.toString());

                List<SHeroInfo> sHeroInfos = new ArrayList<>();

                int sIndex = 1;
                for (Pair<RarityType, Double> rarityType : rarityConfig) {
                    int rarityAmount = (int) Math.round((rarityType.getRight() * poolSize));
                    for (Pair<JobType, Double> jobType : jobConfig) {
                        int jobAmount = (int) Math.round(jobType.getRight() * rarityAmount);
                        log.info(jobType.getLeft() + " " + jobAmount + " " + rarityAmount + " " + jobType.getRight());
                        while (jobAmount > 0) {
                            for (Pair<String, Integer> kingDom : kingDomConfig) {
                                int kingDomAmount = kingDom.getRight();
                                for (int h = 0; h < kingDomAmount; h++) {
                                    SHeroInfo sHeroInfo = new SHeroInfo();
                                    sHeroInfo.id = poolId + "_" + StringUtils.leftPad(String.valueOf(sIndex), String.valueOf(poolSize).length(), "0");
                                    sHeroInfo.jobType = jobType.getLeft();
                                    sHeroInfo.rarity = rarityType.getLeft();
                                    sHeroInfo.kingDom = kingDom.getLeft();
                                    sHeroInfo.poolId = poolId;
                                    sHeroInfos.add(sHeroInfo);
                                    sIndex++;
                                    jobAmount--;
                                    if (jobAmount <= 0) {
                                        break;
                                    }
                                }
                                if (jobAmount <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                }

                XSSFWorkbook poolBook = new XSSFWorkbook();
                XSSFSheet poolSheet = poolBook.createSheet("data");
                Row row = poolSheet.createRow(0);
                List<String> titles = Arrays.asList("id", "jobType", "rarity", "classId", "poolId", "isActive", "isMinion");
                for (int j = 0; j < titles.size(); j++) {
                    Cell titleCell = row.createCell(j);
                    titleCell.setCellValue(titles.get(j));
                }
                for (int j = 0; j < sHeroInfos.size(); j++) {
                    row = poolSheet.createRow(j + 1);
                    SHeroInfo sHeroInfo = sHeroInfos.get(j);
                    row.createCell(0).setCellValue(sHeroInfo.id);
                    row.createCell(1).setCellValue(sHeroInfo.jobType.ordinal());
                    row.createCell(2).setCellValue(sHeroInfo.rarity.ordinal());
                    row.createCell(3).setCellValue(sHeroInfo.kingDom);
                    row.createCell(4).setCellValue(sHeroInfo.poolId);
                    row.createCell(5).setCellValue(true);
                    row.createCell(6).setCellValue(isMinion);
                }
                exportFile("poolHero/" + poolId + ".xlsx", poolBook);
            }
        }


        return "Done";
    }

    @SneakyThrows
    @PostMapping("/generateHero")
    public String generateMasterHero() {
        log.info("generateHero");
        CmdUtils.createFolder("mHero", false);

        XSSFWorkbook poolBook = new XSSFWorkbook();
        XSSFSheet poolSheet = poolBook.createSheet("data");
        Row row = poolSheet.createRow(0);
        List<String> titles = Arrays.asList(
                "id",
                "avatarPath",
                "modelName",
                "name",
                "description",
                "jobType",
                "rarity",
                "classId",
                "isMinion",
                "pvpPoint",
                "pvpAtk",
                "pvpHp",
                "pvpSpeed",
                "skillIds",
                "pvpSkills"
        );

        String[] avatarPath = {
            "avatars/Heros_Centurion",
            "avatars/Heros_ArixtheTreasureHunter",
            "avatars/Heros_Edric",
            "avatars/Heros_SolKnight",
            "avatars/Heros_DragonSlayer",
            "avatars/Heros_Rogue",
            "avatars/Heros_Ulvrik",
            "avatars/Heros_Zathma",
            "avatars/Heros_ZhaoPing",
            "avatars/Heros_Crusader",
            "avatars/Heros_DeathKnight",
            "avatars/Heros_Paladin",
        };
        String[] avatarPathMinion = {
            "avatars/Minion_AxeWarrior",
            "avatars/Minion_Bandits",
            "avatars/Minion_BlizzardFang",
            "avatars/Minion_BoneSphinx",
            "avatars/Minion_BroodChild",
            "avatars/Minion_BroodMother",
            "avatars/Minion_Druids",
            "avatars/Minion_Dryads",
            "avatars/Minion_ElvesRanger",
            "avatars/Minion_Fenrisulfr",
            "avatars/Minion_FrostGiant",
            "avatars/Minion_FuryAxe",
            "avatars/Minion_Fylgia",
            "avatars/Minion_Manticore",
            "avatars/Minion_NecrosShaman",
            "avatars/Minion_Packs",
            "avatars/Minion_SandGuardiant",
            "avatars/Minion_SandWurm",
            "avatars/Minion_SkeletonWarrior",
            "avatars/Minion_TreantProtector",
            "avatars/Minion_TreasureHunter",
            "avatars/Minion_Troll",
            "avatars/Minion_Undead",
            "avatars/Minion_Unicorn",
            "avatars/Minion_Werewolf",
            "avatars/Minion_Wraith",
        };

        String[] modelName = {
            "Heros_Centurion",
            "Heros_ArixtheTreasureHunter",
            "Heros_Edric",
            "Heros_SolKnight",
            "Heros_DragonSlayer",
            "Heros_Rogue",
            "Heros_Ulvrik",
            "Heros_Zathma",
            "Heros_ZhaoPing",
            "Heros_Crusader",
            "Heros_DeathKnight",
            "Heros_Paladin",

        };
        String[] modelNameMinion = {
            "Minion_AxeWarrior",
            "Minion_Bandits",
            "Minion_BlizzardFang",
            "Minion_BoneSphinx",
            "Minion_BroodChild",
            "Minion_BroodMother",
            "Minion_Druids",
            "Minion_Dryads",
            "Minion_ElvesRanger",
            "Minion_Fenrisulfr",
            "Minion_FrostGiant",
            "Minion_FuryAxe",
            "Minion_Fylgia",
            "Minion_Manticore",
            "Minion_NecrosShaman",
            "Minion_Packs",
            "Minion_SandGuardiant",
            "Minion_SandWurm",
            "Minion_SkeletonWarrior",
            "Minion_TreantProtector",
            "Minion_TreasureHunter",
            "Minion_Troll",
            "Minion_Undead",
            "Minion_Unicorn",
            "Minion_Werewolf",
            "Minion_Wraith",
        };

        String[] name = {
            "Centurion",
            "Arix",
            "Edric",
            "SolKnight",
            "DragonSlayer",
            "Rogue",
            "Ulvrik",
            "Zathma",
            "ZhaoPing",
            "Crusader",
            "DeathKnight",
            "Paladin",

        };

        String[] nameMinion = {
            "AxeWarrior",
            "Bandits",
            "BlizzardFang",
            "BoneSphinx",
            "BroodChild",
            "BroodMother",
            "Druids",
            "Dryads",
            "ElvesRanger",
            "Fenrisulfr",
            "FrostGiant",
            "FuryAxe",
            "Fylgia",
            "Manticore",
            "NecrosShaman",
            "Packs",
            "SandGuardiant",
            "SandWurm",
            "SkeletonWarrior",
            "TreantProtector",
            "TreasureHunter",
            "Troll",
            "Undead",
            "Unicorn",
            "Werewolf",
            "Wraith",
        };

        String[] description = {
            "Centurion",
            "Arix",
            "Edric",
            "SolKnight",
            "DragonSlayer",
            "Rogue",
            "Ulvrik",
            "Zathma",
            "ZhaoPing",
            "Crusader",
            "DeathKnight",
            "Paladin",
        };

        String[] descriptionMinion = {
            "AxeWarrior",
            "Bandits",
            "BlizzardFang",
            "BoneSphinx",
            "BroodChild",
            "BroodMother",
            "Druids",
            "Dryads",
            "ElvesRanger",
            "Fenrisulfr",
            "FrostGiant",
            "FuryAxe",
            "Fylgia",
            "Manticore",
            "NecrosShaman",
            "Packs",
            "SandGuardiant",
            "SandWurm",
            "SkeletonWarrior",
            "TreantProtector",
            "TreasureHunter",
            "Troll",
            "Undead",
            "Unicorn",
            "Werewolf",
            "Wraith",
        };

        String[] heroSkill = {
            "\"A0001_S0\",\"A0001_S1\",\"A0001_S2\"",
            "\"A0002_S0\",\"A0002_S1\",\"A0002_S2\"",
            "\"A0003_S0\",\"A0003_S1\",\"A0003_S2\"",
            "\"A0004_S0\",\"A0004_S1\",\"A0004_S2\"",
            "\"A0005_S0\",\"A0005_S1\",\"A0005_S2\"",
            "\"A0006_S0\",\"A0006_S1\",\"A0006_S2\"",
            "\"A0007_S0\",\"A0007_S1\",\"A0007_S2\"",
            "\"A0008_S0\",\"A0008_S1\",\"A0008_S2\"",
            "\"A0009_S0\",\"A0009_S1\",\"A0009_S2\"",
            "\"A0010_S0\",\"A0010_S1\",\"A0010_S2\"",
            "\"A0011_S0\",\"A0011_S1\",\"A0011_S2\"",
            "\"A0012_S0\",\"A0012_S1\",\"A0012_S2\"",

        };
        String[] minionSkill = {
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
            "\"M0002_S0\",\"M0002_S1\"",
        };

        int[] jobType = {0, 1, 2, 3, 4, 5, 6};
        int[] rarity = {0, 1, 2, 3, 4, 5};
        String[] classId = {
                "MCL0001",
                "MCL0002",
                "MCL0003",
                "MCL0004",
                "MCL0005",
                "MCL0006",
                "MCL0007",
                "MCL0008",
                "MCL0009",
                "MCL0010",
                "MCL0011",
                "MCL0012"
        };

        String[] pvpSkills = {
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
            "\"S0003\"",
            "\"S0004\"",
            "\"S0005\"",
            "\"S0006\"",
            "\"S0001\"",
            "\"S0002\"",
        };

        for (int j = 0; j < titles.size(); j++) {
            Cell titleCell = row.createCell(j);
            titleCell.setCellValue(titles.get(j));
        }

        int rowIndex = 1;
        for (int i : jobType) {
            for (int j : rarity) {
                for (String k : classId) {
                    row = poolSheet.createRow(rowIndex);
                    row.createCell(0).setCellValue("A" + StringUtils.leftPad(String.valueOf(rowIndex), 7, "0"));
                    row.createCell(1).setCellValue(avatarPath[rowIndex % avatarPath.length]);
                    row.createCell(2).setCellValue(modelName[rowIndex % modelName.length]);
                    row.createCell(3).setCellValue(name[rowIndex % name.length]);
                    row.createCell(4).setCellValue(description[rowIndex % description.length]);
                    row.createCell(5).setCellValue(i);
                    row.createCell(6).setCellValue(j);
                    row.createCell(7).setCellValue(k);
                    row.createCell(8).setCellValue(false);
                    row.createCell(9).setCellValue((j+1)*100);
                    row.createCell(10).setCellValue(1);
                    row.createCell(11).setCellValue(j+1);
                    row.createCell(12).setCellValue(100);
                    row.createCell(13).setCellValue(heroSkill[rowIndex % heroSkill.length]);
                    row.createCell(14).setCellValue(pvpSkills[rowIndex % pvpSkills.length]);
                    rowIndex++;
                }
            }
        }
        int minionIndex = 1;
        for (int i : jobType) {
            for (int j : rarity) {
                for (String k : classId) {
                    row = poolSheet.createRow(rowIndex);
                    row.createCell(0).setCellValue("M" + StringUtils.leftPad(String.valueOf(minionIndex), 7, "0"));
                    row.createCell(1).setCellValue(avatarPathMinion[minionIndex % avatarPathMinion.length]);
                    row.createCell(2).setCellValue(modelNameMinion[minionIndex % modelNameMinion.length]);
                    row.createCell(3).setCellValue(nameMinion[minionIndex % nameMinion.length]);
                    row.createCell(4).setCellValue(descriptionMinion[minionIndex % descriptionMinion.length]);
                    row.createCell(5).setCellValue(i);
                    row.createCell(6).setCellValue(j);
                    row.createCell(7).setCellValue(k);
                    row.createCell(8).setCellValue(true);
                    row.createCell(9).setCellValue((j+1)*100);
                    row.createCell(10).setCellValue(1);
                    row.createCell(11).setCellValue(j+1);
                    row.createCell(12).setCellValue(100);
                    row.createCell(13).setCellValue(minionSkill[minionIndex % minionSkill.length]);
                    row.createCell(14).setCellValue(pvpSkills[rowIndex % pvpSkills.length]);
                    rowIndex++;
                    minionIndex++;
                }
            }
        }

//        for (int j = 0; j < sHeroInfos.size(); j++) {
//            row = poolSheet.createRow(j + 1);
//            SHeroInfo sHeroInfo = sHeroInfos.get(j);
//            row.createCell(0).setCellValue(sHeroInfo.id);
//            row.createCell(1).setCellValue(sHeroInfo.jobType.ordinal());
//            row.createCell(2).setCellValue(sHeroInfo.rarity.ordinal());
//            row.createCell(3).setCellValue(sHeroInfo.kingDom);
//            row.createCell(4).setCellValue(sHeroInfo.poolId);
//            row.createCell(5).setCellValue(true);
//        }
        exportFile("mHero/MHero.xlsx", poolBook);
        return "Done";
    }

    @SneakyThrows
    @GetMapping("/getPool")
    public ResponseEntity<Object> getPool() {
        String sourcePath = "poolHero";
        String sinkPath = "poolHero/" + "pool_data_init.zip";
        compress(sourcePath, sinkPath);
        File file = new File(sinkPath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pool_data_init.zip");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @SneakyThrows
    @PostMapping("/settingData")
    public String importSettingData(List<MultipartFile> files, boolean isOverride, String modelName) {
        log.info("importSettingData: {}", files.size());
        for (MultipartFile file : files) {
//            String modelName = file.getOriginalFilename().replace(".xlsx", "");
            log.info("Import file: {}", modelName);
            BaseModel model = modelMapper.get(modelName);
            List<Field> declaredFields = Arrays.stream(model.getClass().getSuperclass().getDeclaredFields()).collect(Collectors.toList());
            declaredFields.addAll(Arrays.stream(model.getClass().getDeclaredFields()).collect(Collectors.toList()));

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            int numberOfSheets = workbook.getNumberOfSheets();
//            ArrayNode arrayNodes = objectMapper.createArrayNode();
            List<HashMap<String, Object>> arrayNodes = new ArrayList<>();

            for (int i = 0; i < numberOfSheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rows = sheet.iterator();
                boolean isTitle = true;
                while (rows.hasNext()) {
                    Row row = rows.next();
                    if (isTitle) {
                        isTitle = false;
                        continue;
                    }

                    if (row.getCell(0) == null || StringUtils.isEmpty(String.valueOf(row.getCell(0)))) {
                        break;
                    }
//                    ObjectNode obj = objectMapper.createObjectNode();
                    HashMap<String, Object> obj = new HashMap<>();

                    for (int j = 0; j < declaredFields.size(); j++) {
                        Cell cell = row.getCell(j);
                        Field field = declaredFields.get(j);

                        if (field.getType() instanceof Class && field.getType().isEnum()) {
                            obj.put(field.getName(), (int) cell.getNumericCellValue());
                        } else if (field.getType().isAssignableFrom(int.class)) {
                            obj.put(field.getName(), (int) cell.getNumericCellValue());
                        } else if (field.getType().isAssignableFrom(boolean.class)) {
                            obj.put(field.getName(), cell.getBooleanCellValue());
                        } else if (field.getType().isAssignableFrom(float.class)) {
                            obj.put(field.getName(), (float) cell.getNumericCellValue());
                        } else if (field.getType().isAssignableFrom(String.class)) {
                            obj.put(field.getName(), cell.getStringCellValue());
                        } else if (field.getType().isAssignableFrom(Date.class)) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = formatter.parse(cell.getStringCellValue());
                            obj.put(field.getName(), date);
                        } else if (field.getType().isAssignableFrom(List.class)) {
                            ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
                            Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
                            if (integerListClass.isAssignableFrom(String.class)) {
                                String value = cell.getStringCellValue();
                                String[] split = value.split("\"");
//                                ArrayNode array = objectMapper.createArrayNode();
                                List<Object> array = new ArrayList<>();
                                for (String s : split) {
                                    if (StringUtils.isEmpty(s) || s.equals(",")) {
                                        continue;
                                    }
                                    array.add(s);
                                }
                                obj.put(field.getName(), array);
                            } else if (integerListClass.isAssignableFrom(Integer.class)) {
                                String value = cell.getStringCellValue();
                                String[] split = value.split(",");
//                                ArrayNode array = objectMapper.createArrayNode();
                                List<Object> array = new ArrayList<>();
                                for (String s : split) {
                                    array.add(Integer.parseInt(s));
                                }
                                obj.put(field.getName(), array);
                            } else if (integerListClass.isAssignableFrom(Float.class)) {
                                String value = cell.getStringCellValue();
                                String[] split = value.split(",");
//                                ArrayNode array = objectMapper.createArrayNode();
                                List<Object> array = new ArrayList<>();
                                for (String s : split) {
                                    array.add(NumberUtils.toFloat(s, 1.0f));
                                }
                                obj.put(field.getName(), array);
                            }
                        }
                    }
                    arrayNodes.add(obj);
                }

            }

            DBCollection collection = DataRepository.settingDatabase().getCollection(modelName);
            if (isOverride) {
                BasicDBObject doc = new BasicDBObject();
                collection.remove(doc);
            }

            for (HashMap<String, Object> node : arrayNodes) {
                BasicDBObject doc = new BasicDBObject();
                doc.putAll(node);
                collection.insert(doc);
            }

//            byte[] bytesOfMessage = arrayNodes.toString().getBytes("UTF-8");
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] digest = md.digest(bytesOfMessage);
//            BigInteger bigInt = new BigInteger(1, digest);
//            String hashtext = bigInt.toString(16);
//            while (hashtext.length() < 32) {
//                hashtext = "0" + hashtext;
//            }
//            DBObject whereClause = new BasicDBObject("mModel", modelName);
//            BasicDBObject value = new BasicDBObject();
//            value.put("mModel", modelName);
//            value.put("md5", hashtext);
//            WriteResult result = mDataHash.update(whereClause, value);
//            if (!result.isUpdateOfExisting()) {
//                mDataHash.insert(value);
//            }
            log.info("{} data: {}", model.getClass().getSimpleName(), arrayNodes);
        }
        return "Done";
    }

    private void exportFile(String fileName, Workbook workbook) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
