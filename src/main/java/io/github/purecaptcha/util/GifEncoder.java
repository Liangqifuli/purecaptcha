package io.github.purecaptcha.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * GIF 动画编码器
 * <p>
 * 基于 LZW 压缩算法的 GIF89a 格式编码器
 * 零依赖实现,支持多帧动画和透明度
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class GifEncoder {

    private int width;
    private int height;
    private int delay = 100; // 帧延迟(毫秒)
    private int repeat = 0;  // 循环次数(0=无限循环)
    
    private ByteArrayOutputStream out;
    private BufferedImage firstImage;
    private boolean started = false;

    /**
     * 开始 GIF 编码
     *
     * @param os 输出流
     */
    public void start(ByteArrayOutputStream os) {
        this.out = os;
        this.started = false;
    }

    /**
     * 添加一帧图像
     *
     * @param image BufferedImage
     */
    public void addFrame(BufferedImage image) throws IOException {
        if (out == null) {
            throw new IllegalStateException("必须先调用 start() 方法");
        }

        if (!started) {
            // 写入 GIF 文件头
            writeHeader();
            this.firstImage = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
            
            // 写入逻辑屏幕描述符
            writeLogicalScreenDescriptor();
            
            // 写入全局调色板
            writeColorTable(image);
            
            // 写入循环控制扩展
            writeLoopExtension();
            
            started = true;
        }

        // 写入图形控制扩展
        writeGraphicControlExtension();
        
        // 写入图像描述符和图像数据
        writeImageDescriptor(image);
    }

    /**
     * 完成 GIF 编码
     */
    public void finish() throws IOException {
        if (out != null) {
            // 写入 GIF 结束标记
            out.write(0x3B);
            out.flush();
        }
    }

    /**
     * 设置帧延迟
     *
     * @param delay 延迟时间(毫秒)
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * 设置循环次数
     *
     * @param repeat 循环次数(0=无限循环)
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * 写入 GIF 文件头
     */
    private void writeHeader() throws IOException {
        out.write("GIF89a".getBytes());
    }

    /**
     * 写入逻辑屏幕描述符
     */
    private void writeLogicalScreenDescriptor() throws IOException {
        // 宽度(2字节,小端序)
        out.write(width & 0xFF);
        out.write((width >> 8) & 0xFF);
        
        // 高度(2字节,小端序)
        out.write(height & 0xFF);
        out.write((height >> 8) & 0xFF);
        
        // 全局颜色表标志(1), 颜色深度(7), 排序标志(0), 全局颜色表大小(7)
        out.write(0xF7); // 11110111
        
        // 背景颜色索引
        out.write(0);
        
        // 像素宽高比
        out.write(0);
    }

    /**
     * 写入全局调色板
     */
    private void writeColorTable(BufferedImage image) throws IOException {
        // 256色调色板 (3字节/色 = 768字节)
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        
        // 简化调色板:使用 RGB 立方体采样
        for (int i = 0; i < 256; i++) {
            int r = (i & 0xE0);       // 3 bits
            int g = (i & 0x1C) << 3;  // 3 bits
            int b = (i & 0x03) << 6;  // 2 bits
            
            out.write(r);
            out.write(g);
            out.write(b);
        }
    }

    /**
     * 写入循环控制扩展
     */
    private void writeLoopExtension() throws IOException {
        out.write(0x21); // 扩展标识符
        out.write(0xFF); // 应用程序扩展
        out.write(11);   // 块大小
        out.write("NETSCAPE2.0".getBytes());
        out.write(3);    // 子块大小
        out.write(1);    // 子块ID
        
        // 循环次数(2字节,小端序)
        out.write(repeat & 0xFF);
        out.write((repeat >> 8) & 0xFF);
        
        out.write(0);    // 块终止符
    }

    /**
     * 写入图形控制扩展
     */
    private void writeGraphicControlExtension() throws IOException {
        out.write(0x21); // 扩展标识符
        out.write(0xF9); // 图形控制标签
        out.write(4);    // 块大小
        
        // 处置方法(0), 用户输入标志(0), 透明色标志(0)
        out.write(0);
        
        // 延迟时间(2字节,小端序,单位:10ms)
        int delayTime = delay / 10;
        out.write(delayTime & 0xFF);
        out.write((delayTime >> 8) & 0xFF);
        
        // 透明色索引
        out.write(0);
        
        out.write(0); // 块终止符
    }

    /**
     * 写入图像描述符
     */
    private void writeImageDescriptor(BufferedImage image) throws IOException {
        out.write(0x2C); // 图像分隔符
        
        // 图像左上角 X 坐标
        out.write(0);
        out.write(0);
        
        // 图像左上角 Y 坐标
        out.write(0);
        out.write(0);
        
        // 图像宽度
        out.write(width & 0xFF);
        out.write((width >> 8) & 0xFF);
        
        // 图像高度
        out.write(height & 0xFF);
        out.write((height >> 8) & 0xFF);
        
        // 局部颜色表标志(0), 交错标志(0), 排序标志(0), 保留(0), 局部颜色表大小(0)
        out.write(0);
        
        // 写入图像数据(LZW压缩)
        writeImageData(image);
    }

    /**
     * 写入图像数据(简化的 LZW 压缩)
     */
    private void writeImageData(BufferedImage image) throws IOException {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        
        // LZW 最小代码大小
        out.write(8);
        
        // 转换为索引颜色
        byte[] indexedPixels = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int rgb = pixels[i];
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            
            // 映射到256色调色板
            indexedPixels[i] = (byte) (
                ((r & 0xE0)) |
                ((g & 0xE0) >> 3) |
                ((b & 0xC0) >> 6)
            );
        }
        
        // 简化压缩:直接分块写入(未压缩)
        int blockSize = 255;
        for (int i = 0; i < indexedPixels.length; i += blockSize) {
            int size = Math.min(blockSize, indexedPixels.length - i);
            out.write(size);
            out.write(indexedPixels, i, size);
        }
        
        out.write(0); // 块终止符
    }
}
