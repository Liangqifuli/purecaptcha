import java.io.File;

/**
 * 检查所有11张背景图片的详细信息
 */
public class Check11Backgrounds {

    public static void main(String[] args) {
        String resourcePath = "../src/main/resources/slider-backgrounds/";
        File dir = new File(resourcePath);
        
        System.out.println("=== 11张背景图片详细信息 ===\n");
        System.out.println("序号 | 文件名                      | 大小      | 类型");
        System.out.println("-----|----------------------------|-----------|------");
        
        File[] files = dir.listFiles();
        long totalSize = 0;
        int count = 0;
        
        if (files != null) {
            // 排序
            java.util.Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
            
            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg"))) {
                    count++;
                    long size = file.length();
                    totalSize += size;
                    String sizeStr = String.format("%.2f KB", size / 1024.0);
                    String type = file.getName().endsWith(".png") ? "PNG" : "JPG";
                    System.out.printf("%4d | %-26s | %9s | %s\n", 
                        count, file.getName(), sizeStr, type);
                }
            }
        }
        
        System.out.println("-----|----------------------------|-----------|------");
        System.out.printf("总计 | %d 张                       | %7.2f KB |\n", 
            count, totalSize / 1024.0);
        
        // 评估
        System.out.println("\n=== 评估结果 ===");
        System.out.println("✅ 图片数量: " + count + " 张");
        System.out.printf("✅ 总大小: %.2f KB (%.2f MB)\n", totalSize / 1024.0, totalSize / 1024.0 / 1024.0);
        System.out.printf("✅ 平均大小: %.2f KB\n", (totalSize / 1024.0) / count);
        
        if (totalSize / 1024.0 < 500) {
            System.out.println("✅ jar包增加: 约" + String.format("%.0f", totalSize / 1024.0) + "KB - 非常合适！");
        } else if (totalSize / 1024.0 < 1024) {
            System.out.println("✅ jar包增加: 约" + String.format("%.0f", totalSize / 1024.0) + "KB - 可以接受");
        } else if (totalSize / 1024.0 < 2048) {
            System.out.println("⚠️  jar包增加: 约" + String.format("%.2f", totalSize / 1024.0 / 1024.0) + "MB - 稍大");
        } else {
            System.out.println("❌ jar包增加: 约" + String.format("%.2f", totalSize / 1024.0 / 1024.0) + "MB - 过大！");
        }
        
        // 详细分析
        System.out.println("\n=== 详细分析 ===");
        int pngCount = 0, jpgCount = 0;
        long pngSize = 0, jpgSize = 0;
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".png")) {
                        pngCount++;
                        pngSize += file.length();
                    } else if (file.getName().endsWith(".jpg")) {
                        jpgCount++;
                        jpgSize += file.length();
                    }
                }
            }
        }
        
        System.out.printf("PNG格式: %d张, %.2f KB (平均 %.2f KB)\n", 
            pngCount, pngSize / 1024.0, pngCount > 0 ? (pngSize / 1024.0) / pngCount : 0);
        System.out.printf("JPG格式: %d张, %.2f KB (平均 %.2f KB)\n", 
            jpgCount, jpgSize / 1024.0, jpgCount > 0 ? (jpgSize / 1024.0) / jpgCount : 0);
        
        System.out.println("\n📊 结论: " + (totalSize / 1024.0 < 500 ? "完全可行！" : "可以使用，建议优化大文件"));
    }
}

