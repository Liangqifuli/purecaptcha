/**
 * 前端验证码工具类
 * 
 * 功能：
 * - 支持5种验证码：字符、算术、中文、GIF动图、滑动拼图
 * - 自动生成验证码显示界面
 * - 处理用户交互和验证
 * - 与后端API无缝对接
 * 
 * 使用示例：
 * 
 * // 1. 初始化验证码
 * const captcha = new CaptchaUtil({
 *     container: '#captcha-container',
 *     apiBaseUrl: '/api/captcha',
 *     type: 'ARITHMETIC',
 *     onSuccess: (result) => {
 *         console.log('验证成功', result);
 *     },
 *     onError: (error) => {
 *         console.log('验证失败', error);
 *     }
 * });
 * 
 * // 2. 生成验证码
 * captcha.generate();
 * 
 * // 3. 验证（自动在用户提交时调用）
 * captcha.verify(userAnswer);
 * 
 * @author PureCaptcha
 * @version 1.0.0
 */
class CaptchaUtil {
    /**
     * 构造函数
     * 
     * @param {Object} options 配置选项
     * @param {String} options.container 容器选择器
     * @param {String} options.apiBaseUrl API基础URL（默认：/api/captcha）
     * @param {String} options.type 验证码类型（默认：ALPHANUMERIC）
     * @param {Number} options.width 宽度（默认：200）
     * @param {Number} options.height 高度（默认：80）
     * @param {Function} options.onSuccess 验证成功回调
     * @param {Function} options.onError 验证失败回调
     * @param {Function} options.onGenerate 生成成功回调
     */
    constructor(options = {}) {
        this.container = document.querySelector(options.container || '#captcha-container');
        this.apiBaseUrl = options.apiBaseUrl || '/api/captcha';
        this.type = options.type || 'ALPHANUMERIC';
        this.width = options.width || 200;
        this.height = options.height || 80;
        this.onSuccess = options.onSuccess || (() => {});
        this.onError = options.onError || (() => {});
        this.onGenerate = options.onGenerate || (() => {});
        
        this.captchaId = null;
        this.captchaData = null;
        
        this.init();
    }
    
    /**
     * 初始化界面
     */
    init() {
        if (!this.container) {
            console.error('CaptchaUtil: 容器元素不存在');
            return;
        }
        
        // 根据类型创建不同的界面
        if (this.type === 'SLIDER') {
            this.initSliderUI();
        } else {
            this.initNormalUI();
        }
    }
    
    /**
     * 初始化普通验证码界面
     */
    initNormalUI() {
        this.container.innerHTML = `
            <div class="captcha-wrapper" style="max-width: ${this.width}px;">
                <div class="captcha-display" style="position: relative; background: #f5f7fa; border: 1px solid #e1e4e8; border-radius: 6px; padding: 12px; margin-bottom: 12px;">
                    <img id="captcha-image" style="display: block; width: 100%; border-radius: 4px; cursor: pointer;" alt="验证码">
                    <div class="captcha-loading" style="display: none; text-align: center; padding: 20px; color: #6a737d;">
                        加载中...
                    </div>
                </div>
                <div class="captcha-input" style="margin-bottom: 12px;">
                    <input type="text" id="captcha-input" placeholder="请输入验证码" 
                           style="width: 100%; padding: 10px 12px; border: 1px solid #d1d5da; border-radius: 6px; font-size: 14px;">
                </div>
                <div class="captcha-actions" style="display: flex; gap: 8px;">
                    <button id="captcha-verify-btn" 
                            style="flex: 1; padding: 10px 16px; background: #2ea44f; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500;">
                        验证
                    </button>
                    <button id="captcha-refresh-btn" 
                            style="padding: 10px 16px; background: white; color: #24292e; border: 1px solid #d1d5da; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500;">
                        刷新
                    </button>
                </div>
                <div class="captcha-result" id="captcha-result" style="margin-top: 12px; padding: 12px; border-radius: 6px; font-size: 14px; display: none;"></div>
            </div>
        `;
        
        // 绑定事件
        this.container.querySelector('#captcha-image').addEventListener('click', () => this.generate());
        this.container.querySelector('#captcha-refresh-btn').addEventListener('click', () => this.generate());
        this.container.querySelector('#captcha-verify-btn').addEventListener('click', () => this.verifyNormal());
        this.container.querySelector('#captcha-input').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.verifyNormal();
        });
    }
    
    /**
     * 初始化滑动验证码界面
     */
    initSliderUI() {
        this.container.innerHTML = `
            <div class="slider-captcha-wrapper" style="max-width: 350px;">
                <div class="slider-display" style="position: relative; margin-bottom: 16px;">
                    <img id="slider-bg-img" style="display: block; width: 100%; border-radius: 6px; border: 1px solid #e1e4e8;" alt="背景图">
                    <div id="slider-piece" style="position: absolute; display: none; cursor: move;">
                        <img id="slider-piece-img" style="display: block; width: 60px; height: 60px; box-shadow: 0 2px 8px rgba(0,0,0,0.15);" alt="滑块">
                    </div>
                    <div class="slider-loading" style="display: none; text-align: center; padding: 40px; color: #6a737d; background: #f5f7fa; border-radius: 6px;">
                        加载中...
                    </div>
                </div>
                <div class="slider-track" style="position: relative; height: 44px; background: #f3f4f6; border: 1px solid #d1d5da; border-radius: 6px; overflow: hidden;">
                    <div class="slider-track-fill" style="position: absolute; left: 0; top: 0; height: 100%; background: #0969da; width: 0; transition: width 0.1s;"></div>
                    <div class="slider-button" id="slider-button" 
                         style="position: absolute; left: 0; top: 0; width: 44px; height: 44px; background: white; border: 1px solid #d1d5da; border-radius: 6px; cursor: grab; display: flex; align-items: center; justify-content: center; font-size: 18px; color: #0969da; user-select: none; z-index: 10;">
                        ››
                    </div>
                    <div class="slider-text" style="position: absolute; left: 0; right: 0; top: 0; height: 100%; display: flex; align-items: center; justify-content: center; color: #6a737d; font-size: 14px; pointer-events: none;">
                        拖动滑块完成验证
                    </div>
                </div>
                <div class="slider-actions" style="display: flex; gap: 8px; margin-top: 12px;">
                    <button id="slider-refresh-btn" 
                            style="flex: 1; padding: 10px 16px; background: white; color: #24292e; border: 1px solid #d1d5da; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500;">
                        刷新
                    </button>
                </div>
                <div class="slider-result" id="slider-result" style="margin-top: 12px; padding: 12px; border-radius: 6px; font-size: 14px; display: none;"></div>
            </div>
        `;
        
        // 绑定滑动事件
        this.initSliderDrag();
        
        // 绑定刷新事件
        this.container.querySelector('#slider-refresh-btn').addEventListener('click', () => this.generate());
    }
    
    /**
     * 初始化滑动拖拽
     */
    initSliderDrag() {
        const button = this.container.querySelector('#slider-button');
        const track = this.container.querySelector('.slider-track');
        const fill = this.container.querySelector('.slider-track-fill');
        const piece = this.container.querySelector('#slider-piece');
        
        let isDragging = false;
        let startX = 0;
        let currentX = 0;
        
        const onMouseDown = (e) => {
            isDragging = true;
            startX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX;
            button.style.cursor = 'grabbing';
        };
        
        const onMouseMove = (e) => {
            if (!isDragging) return;
            
            const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX;
            const deltaX = clientX - startX;
            const maxX = track.offsetWidth - button.offsetWidth;
            
            currentX = Math.max(0, Math.min(deltaX, maxX));
            
            button.style.left = currentX + 'px';
            fill.style.width = currentX + 'px';
            
            if (piece.style.display !== 'none' && this.captchaData) {
                const bgImg = this.container.querySelector('#slider-bg-img');
                const scale = bgImg.offsetWidth / 350;
                piece.style.left = (currentX / scale) + 'px';
            }
        };
        
        const onMouseUp = () => {
            if (!isDragging) return;
            isDragging = false;
            button.style.cursor = 'grab';
            
            // 验证位置
            if (this.captchaId && currentX > 0) {
                const bgImg = this.container.querySelector('#slider-bg-img');
                const scale = bgImg.offsetWidth / 350;
                const userX = Math.round(currentX / scale);
                this.verifySlider(userX);
            }
        };
        
        // PC端事件
        button.addEventListener('mousedown', onMouseDown);
        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
        
        // 移动端事件
        button.addEventListener('touchstart', onMouseDown, { passive: true });
        document.addEventListener('touchmove', onMouseMove, { passive: true });
        document.addEventListener('touchend', onMouseUp);
    }
    
    /**
     * 生成验证码
     */
    async generate() {
        try {
            this.showLoading(true);
            this.clearResult();
            
            const response = await fetch(`${this.apiBaseUrl}/generate?type=${this.type}`);
            const data = await response.json();
            
            if (data.success) {
                this.captchaId = data.data.captchaId;
                this.captchaData = data.data;
                
                if (this.type === 'SLIDER') {
                    this.displaySlider(data.data);
                } else {
                    this.displayNormal(data.data);
                }
                
                this.onGenerate(data.data);
            } else {
                this.showResult(false, data.message || '生成失败');
            }
        } catch (error) {
            this.showResult(false, '网络错误：' + error.message);
        } finally {
            this.showLoading(false);
        }
    }
    
    /**
     * 显示普通验证码
     */
    displayNormal(data) {
        const img = this.container.querySelector('#captcha-image');
        img.src = 'data:image/png;base64,' + data.imageBase64;
        img.style.display = 'block';
        
        // 清空输入框
        const input = this.container.querySelector('#captcha-input');
        if (input) {
            input.value = '';
            input.focus();
        }
    }
    
    /**
     * 显示滑动验证码
     */
    displaySlider(data) {
        const bgImg = this.container.querySelector('#slider-bg-img');
        const piece = this.container.querySelector('#slider-piece');
        const pieceImg = this.container.querySelector('#slider-piece-img');
        const button = this.container.querySelector('#slider-button');
        const fill = this.container.querySelector('.slider-track-fill');
        
        // 显示背景图
        bgImg.src = 'data:image/png;base64,' + data.imageBase64;
        bgImg.style.display = 'block';
        
        // 显示滑块
        pieceImg.src = 'data:image/png;base64,' + data.sliderImageBase64;
        
        bgImg.onload = () => {
            const scale = bgImg.offsetWidth / 350;
            piece.style.top = (data.sliderY * scale) + 'px';
            piece.style.left = '0px';
            piece.style.display = 'block';
            
            pieceImg.style.width = (60 * scale) + 'px';
            pieceImg.style.height = (60 * scale) + 'px';
        };
        
        // 重置滑块位置
        button.style.left = '0px';
        fill.style.width = '0px';
    }
    
    /**
     * 验证普通验证码
     */
    async verifyNormal() {
        const input = this.container.querySelector('#captcha-input');
        const answer = input.value.trim();
        
        if (!answer) {
            this.showResult(false, '请输入验证码');
            return;
        }
        
        if (!this.captchaId) {
            this.showResult(false, '请先生成验证码');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiBaseUrl}/verify`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    captchaId: this.captchaId,
                    answer: answer
                })
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.showResult(true, '验证成功！');
                this.onSuccess(data);
            } else {
                this.showResult(false, data.message || '验证失败');
                this.onError(data);
                // 验证失败后刷新验证码
                setTimeout(() => this.generate(), 1500);
            }
        } catch (error) {
            this.showResult(false, '网络错误：' + error.message);
        }
    }
    
    /**
     * 验证滑动验证码
     */
    async verifySlider(userX) {
        if (!this.captchaId) {
            this.showResult(false, '请先生成验证码');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiBaseUrl}/verify`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    captchaId: this.captchaId,
                    answer: userX.toString()
                })
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.showResult(true, '验证成功！');
                this.lockSlider();
                this.onSuccess(data);
            } else {
                this.showResult(false, data.message || '验证失败');
                this.onError(data);
                // 验证失败后重置
                setTimeout(() => {
                    this.resetSlider();
                    this.generate();
                }, 1500);
            }
        } catch (error) {
            this.showResult(false, '网络错误：' + error.message);
        }
    }
    
    /**
     * 锁定滑块
     */
    lockSlider() {
        const button = this.container.querySelector('#slider-button');
        button.style.cursor = 'not-allowed';
        button.style.pointerEvents = 'none';
    }
    
    /**
     * 重置滑块
     */
    resetSlider() {
        const button = this.container.querySelector('#slider-button');
        const fill = this.container.querySelector('.slider-track-fill');
        const piece = this.container.querySelector('#slider-piece');
        
        button.style.left = '0px';
        button.style.cursor = 'grab';
        button.style.pointerEvents = 'auto';
        fill.style.width = '0px';
        piece.style.left = '0px';
    }
    
    /**
     * 显示加载状态
     */
    showLoading(show) {
        if (this.type === 'SLIDER') {
            const loading = this.container.querySelector('.slider-loading');
            const display = this.container.querySelector('.slider-display');
            if (loading && display) {
                loading.style.display = show ? 'block' : 'none';
                this.container.querySelector('#slider-bg-img').style.display = show ? 'none' : 'block';
            }
        } else {
            const loading = this.container.querySelector('.captcha-loading');
            const img = this.container.querySelector('#captcha-image');
            if (loading && img) {
                loading.style.display = show ? 'block' : 'none';
                img.style.display = show ? 'none' : 'block';
            }
        }
    }
    
    /**
     * 显示结果
     */
    showResult(success, message) {
        const resultId = this.type === 'SLIDER' ? '#slider-result' : '#captcha-result';
        const result = this.container.querySelector(resultId);
        
        if (result) {
            result.textContent = message;
            result.style.display = 'block';
            result.style.background = success ? '#d4edda' : '#f8d7da';
            result.style.color = success ? '#155724' : '#721c24';
            result.style.border = `1px solid ${success ? '#c3e6cb' : '#f5c6cb'}`;
        }
    }
    
    /**
     * 清除结果
     */
    clearResult() {
        const resultId = this.type === 'SLIDER' ? '#slider-result' : '#captcha-result';
        const result = this.container.querySelector(resultId);
        if (result) {
            result.style.display = 'none';
        }
    }
    
    /**
     * 获取当前验证码ID
     */
    getCaptchaId() {
        return this.captchaId;
    }
    
    /**
     * 获取验证码数据
     */
    getCaptchaData() {
        return this.captchaData;
    }
    
    /**
     * 销毁验证码实例
     */
    destroy() {
        if (this.container) {
            this.container.innerHTML = '';
        }
        this.captchaId = null;
        this.captchaData = null;
    }
}

// 支持ES6模块导出
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CaptchaUtil;
}

