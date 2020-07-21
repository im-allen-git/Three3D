
			mui.init({
				statusBarBackground: '#f7f7f7'
			});
			var aniShow = "pop-in";
			var menu = null,
				showMenu = false;
			var isInTransition = false;
			var _self;
			//只有ios支持的功能需要在Android平台隐藏；
			if(mui.os.android) {
				var list = document.querySelectorAll('.ios-only');
				if(list) {
					for(var i = 0; i < list.length; i++) {
						list[i].style.display = 'none';
					}
				}
				//Android平台暂时使用slide-in-right动画
				if(parseFloat(mui.os.version) < 4.4) {
					aniShow = "slide-in-right";
				}
			}
			//初始化，并预加载webview模式的选项卡			
			function preload() {

				var menu_style = {
					left: "-70%",
					width: '70%',
					popGesture: "none",
					render:"always"
				};

				if(mui.os.ios) {
					menu_style.zindex = -1;
				}

				//处理侧滑导航，为了避免和子页面初始化等竞争资源，延迟加载侧滑页面；
				menu = mui.openWindow({
					id: 'index-menu',
					url: 'index-menu.html',
					styles: menu_style,
					show: {
						aniShow: 'none'
					},
					waiting: {
						autoShow: false
					}
				});
			}
			mui.plusReady(function() {
				//读取本地存储，检查是否为首次启动
				var showGuide = plus.storage.getItem("lauchFlag");
				//仅支持竖屏显示
				plus.screen.lockOrientation("portrait-primary");
				if(showGuide) {
					//有值，说明已经显示过了，无需显示；
					//关闭splash页面；
					plus.navigator.closeSplashscreen();
					plus.navigator.setFullscreen(false);
					//预加载
					preload();
				} else {
					//显示启动导航
					mui.openWindow({
						id: 'guide',
						url: 'examples/guide.html',
						styles: {
							popGesture: "none"
						},
						show: {
							aniShow: 'none'
						},
						waiting: {
							autoShow: false
						}
					});
					//延迟的原因：优先打开启动导航页面，避免资源争夺
					setTimeout(function() {
						//预加载
						preload();
					}, 200);
				}

				//绘制顶部图标
				_self = plus.webview.currentWebview();
				var titleView = _self.getNavigationbar();

				if(!titleView) {
					titleView = plus.webview.getLaunchWebview().getNavigationbar();
				}

				titleView.drawRect("#cccccc", {
					top: "43px",
					height:"1px",
					left: "0px"
				}); //绘制底部边线

				//开启回弹
				_self.setStyle({
					bounce: "vertical",
					bounceBackground:"#efeff4",
					popGesture:'none'//首页有侧滑菜单，因此屏蔽首页的侧滑关闭功能
				});

				//绘制左上角menu图标
				var bitmap_menu = new plus.nativeObj.Bitmap("menu");
				bitmap_menu.loadBase64Data("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwBAMAAAClLOS0AAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAKlBMVEUAAAAAev8Aev8Aev8Aev8Aev8Aev8Aev8Aev8Aev8Aev8Aev8Aev8AAABINtoqAAAADHRSTlMA/fPQ0M/e3tzs7OjgY5g4AAAAAWJLR0QAiAUdSAAAAAd0SU1FB+EBFwEbOGGUPSIAAAA2SURBVDjLY2AY9oDxDBZwCJ8EswsW4DrQ/hicgPTQZSvHAioG2h+DE5AeupyrsIDVA+0PqgEAu36BkQX5nBQAAAAldEVYdGRhdGU6Y3JlYXRlADIwMTctMDEtMjNUMDE6Mjc6NTYrMDg6MDC8FK1uAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE3LTAxLTIzVDAxOjI3OjU2KzA4OjAwzUkV0gAAAABJRU5ErkJggg==");
				titleView.drawBitmap(bitmap_menu, {}, {
					top: "10px",
					left: "10px",
					width: "24px",
					height: "24px"
				});

				var about_left = window.innerWidth - 34;
				var bitmap = new plus.nativeObj.Bitmap("about");
				bitmap.loadBase64Data("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAQAAAD9CzEMAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QAAKqNIzIAAAAHdElNRQfhARcBEina5qaZAAAB10lEQVRYw+2XP0vDQBiHf7Wlq63gLAgV/QpOBnEpZHJycBL6Lq2LuFq6Kn6AK2RxKQhu2aS0XQT7DbTgLqVGR4OlDtL8uUu8S3Lape/Wy93z5L1r7t4DlrHoyCXrznZRhEsPWgUsj0PUsC886KKNO5pmErAKWjj6tUsHTRqlErBVXONEaR4snNFHQgHbQ08JPg+D+lHNKzH4ekI80GP1qOZ8JP4ClwnxAFA1Z/ZAQcDqqfAAYJgTexhuEtYg8dwLkvBacAJWxlsmPACskeP/4Bf5KjOeY4QyYBU8SwZv4x2P2JD02vI/vXAGLcnAFj3RK06lOQQ4gQwU5v8FO3Bxg2OpwluHQqDxQDpsE5/SPnPWrThFNcXBKuGxvClieXxpFACFn43cz6CsNMygHAylnmV+ita1vr/HS5qBeggZFDULirzA1SxweYGTEhQXDi8YaxaM/zsDmqKrEd+d10vBraKtUeCxgoJ7jQKPFRCQg44mfMc/NJOeaGoRd6LRCJYGvBWsVflD/1yDIMTgBOQobsbxYVDoixJqU+qjkQHf4EvgiNLRHpqzlHk0SairIotfe2BOUE3x9hFl26LuBwD1UVL+01ooReMXe0fzJH95yxRkie/Jy5DGN/4FegI2+YzMAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDE3LTAxLTIzVDAxOjE4OjQxKzA4OjAw3fCu8gAAACV0RVh0ZGF0ZTptb2RpZnkAMjAxNy0wMS0yM1QwMToxODo0MSswODowMKytFk4AAAAASUVORK5CYII=");
				titleView.drawBitmap(bitmap, {}, {
					top: "10px",
					left: about_left + "px",
					width: "24px",
					height: "24px"
				});

				titleView.interceptTouchEvent(true);
				titleView.addEventListener("click", function(e) {
					var x = e.clientX;
					if(x < 44) { //触发menu菜单
						var _left = parseInt(_self.getStyle().left);
						if(_left > 0) { //处于显示状态
							closeMenu();
						} else {
							openMenu();
						}
					} else if(x > about_left) { //触发关于页面
						var aniShow = mui.os.plus ? "slide-in-right" : "zoom-fade-out";
						mui.openWindow({
							url: "examples/info.html",
							id: "info",
							styles: {
								popGesture: "close",
								statusbar: {
									background: "#f7f7f7"
								}
							},
							show: {
								aniShow: aniShow,
								duration: 300
							}
						});
					}
				}, false);

				//启用侧滑拖拽操作，延时的原因是menu页是延时创建的，所以这里需要相应延时
				setTimeout(function() {
					_self.drag({
						direction: "right",
						moveMode: "followFinger"
					}, {
						view: menu,
						moveMode: "follow"
					}, function(e) {
						//console.log(JSON.stringify(e));
					});
				}, 350);


				// 原生图片预览仅新版本runtime支持，若引擎不支持，则隐藏；
				if(!plus.nativeUI.previewImage) {
					var previewImageNativeElem = document.getElementById('preview_image_native');
					previewImageNativeElem.className = previewImageNativeElem.className.replace('mui-plus-visible', 'mui-hidden');
				}
				
			});
			//主列表点击事件
			mui('#list').on('tap', 'a', function() {
				var href = this.getAttribute('href');
				
				//非plus环境，直接走href跳转
				if(!mui.os.plus) {
					location.href = href;
					return;
				}
				
				var id = this.getAttribute("data-wid");
				if(!id) {
					id = href;
				}

				if(href && ~href.indexOf('.html')) {
					//打开窗口的相关参数
					var options = {
						styles:{
							popGesture: "close"
						},
						extras:{}
					};
					//如下场景不适用下拉回弹：
					//1、单webview下拉刷新；2、底部有fixed定位的div的页面
					if(!~id.indexOf('pullrefresh.html') && !~href.indexOf("examples/tabbar.html") && !~href.indexOf("list-to-detail/listview.html")) {
						options.styles.bounce = "vertical";
					}
					//图标页面需要启动硬件加速
					if(~id.indexOf('icons.html') || ~id.indexOf("echarts.html")) {
						options.styles.hardwareAccelerated = true;
					}
					if(~id.indexOf('im-chat.html')) {
						options.extras.acceleration = "none";
					}
					
					var titleType = this.getAttribute("data-title-type");
					if(titleType && titleType.indexOf("native") > -1) {//原生导航
						options.styles.titleNView = {
							autoBackButton:true,
							backgroundColor:'#f7f7f7',
							titleText:this.innerHTML.trim(),
							splitLine: {
								color: '#cccccc'
							}
						};
						
						options.show = {
							event:'loaded'
						}
						//有原生标题的情况下，就不需要waiting框了
						options.waiting = {
							autoShow:false
						}
						
						//透明渐变导航,增加类型设置
						if(titleType == "transparent_native") {
							options.styles.titleNView.type = "transparent";
						}
						
						//处理原生图片轮播
						if(~id.indexOf("slider-native.html")) {
							options.styles.subNViews = [{ //配置图片轮播
								id: 'slider-native',
								type: 'ImageSlider',
								styles: {
									left: 0,
									right: 0,
									top: 0,
									height: '200px',
									position: 'static',
									loop: true,
									images: [{
										src: '_www/images/yuantiao.jpg',
										width: '100%'
									}, {
										src: '_www/images/shuijiao.jpg',
										width: '100%',
									}, {
										src: '_www/images/muwu.jpg',
										width: '100%',
									}, {
										src: '_www/images/cbd.jpg',
										width: '100%',
									}]
								}
							}];
						}
					}else{
						//非原生导航，需要设置顶部状态栏占位
						options.styles.statusbar = {
							background: "#f7f7f7"
						}
					}
					
					//侧滑菜单需动态控制一下zindex值；
					if(~id.indexOf('offcanvas-')) {
						options.styles.zindex = 9998;
						options.styles.popGesture = ~id.indexOf('offcanvas-with-right') ? "close" : "none";
					}
					
					if(id && id == "viewgroup") { //强制启用截屏
						options.extras.acceleration = "capture";
					}
					//打开新窗口
					mui.openWindow(href,id,options);
				}
			});
			/**
			 * 显示侧滑菜单
			 */
			function openMenu() {
				plus.webview.startAnimation({
						'view': _self,
						'styles': {
							'fromLeft': '0',
							'toLeft': "70%"
						},
						'action': 'show'
					}, {
						'view': menu,
						'styles': {
							'fromLeft': "-70%",
							'toLeft': '0'
						},
						'action': 'show'
					},
					function(e) {
						//console.log(JSON.stringify(e));
						if(e.id == menu.id) { //侧滑菜单打开
						}
					}.bind(this)
				)
			};
			/**
			 * 关闭菜单
			 */
			function closeMenu() {
				plus.webview.startAnimation({
						'view': _self,
						'styles': {
							'fromLeft': '70%',
							'toLeft': "0"
						},
						'action': 'show'
					}, {
						'view': menu,
						'styles': {
							'fromLeft': "0",
							'toLeft': '-70%'
						},
						'action': 'show'
					},
					function(e) {
						console.log(JSON.stringify(e));
						if(e.id == _self.id) {}
					}.bind(this)
				)
			};
			window.addEventListener("menu:close", closeMenu);

			var _toast = false;

			mui.back = function() {
				if(parseInt(_self.getStyle().left) > 0) {
					closeMenu();
					return;
				}

				if(!_toast || !_toast.isVisible()) {
					_toast = mui.toast('再按一次返回键退出<br>点此可&nbsp;<span style="border-bottom:1px solid #fff" onclick="openFeedback();">反馈意见</span>', {
						duration: 'long',
						type: 'div'
					});
				} else {
					plus.runtime.quit();
				}
			}

			//重写mui.menu方法，Android版本menu按键按下可自动打开、关闭侧滑菜单；
			mui.menu = function() {
				if(parseInt(_self.getStyle().left) > 0) {
					closeMenu();
				} else {
					openMenu();
				}
			}

			/**
			 * 退出时提醒用户参加问题反馈
			 */
			function openFeedback() {
				plus.nativeUI.showWaiting();
				var _p = plus.os.name === 'Android' ? 'a' : plus.os.name === 'iOS' ? 'i' : '';
				//TODO：这里使用的是FeedBack云地址，开发者也可以替换为本地页面地址
				var url = 'http://stream.dcloud.net.cn/wap2app/feedback?p=' + _p;
				url += "&plus_version=" + plus.runtime.innerVersion;
				url += "&vendor=" + plus.device.vendor;
				url += "&md=" + plus.device.model;
				/*****开发者需修改的部分   开始*****/
				url += "&app_name=HelloMUI&app_vendor=DCloud";
				//如有本地关于页面，则填写关于页面的路径
				//注意：需要_www/前缀
				url += "&about=_www/examples/info.html";
				/*****开发者需修改的部分   结束*****/

				var feedbackWebview = plus.webview.create(url, "__W2A_FEEDBACK");
				feedbackWebview.addEventListener('titleUpdate', function() {
					plus.nativeUI.closeWaiting();
					feedbackWebview.show('slide-in-right', 300);
				});
			}
		