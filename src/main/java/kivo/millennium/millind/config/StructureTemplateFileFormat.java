package kivo.millennium.millind.config;

import java.util.List;
import java.util.Map;

public class StructureTemplateFileFormat {
    private List<List<List<String>>> pattern;
    private Map<String,List<String>> legend;

    // 必须要有默认构造方法，Gson 才能正确反序列化
    public StructureTemplateFileFormat() {}

    public List<List<List<String>>> getPattern() {
        return pattern;
    }

    public Map<String, List<String>> getLegend() {
        return legend;
    }

    // Setters (可选，如果需要手动设置值)
    public void setPattern(List<List<List<String>>> pattern) {
        this.pattern = pattern;
    }

    public void setLegend(Map<String,List<String>> legend) {
        this.legend = legend;
    }
}