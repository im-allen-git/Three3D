$(function() {
	// 折线图
	var myChart1 = echarts.init(document.getElementById('main1'));
	var option1 = {
		grid: { //配置canves图标父元素div的距离(就是canves与周边距离)
			top:"25px",
		    left:"10px",
		    right:"10px",
		    bottom:"20px",
			width:'auto',
			containLabel: true
		},
		/* title: {
			text: '*最大摄入量每日8克，日均摄入6克',
			textStyle: {
				fontSize: 12,
				fontWeight: 'normal',
				color:'#868c92'
			},
			height:5
		
		},*/
		 tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross'
		        }
		    },
			toolbox: {
			        show: true,
			        feature: {
			            saveAsImage: {}
			        }
			    },
				
		color:'#3aa0ff',
		
		 /* legend: {
			data: ['每日标准 (单位：克)'],
			textStyle:{
				fontSize: 12,
				fontWeight: 'normal',
				color:'#868c92',				
			},
			itemWidth:20,			
			itemHeight:5,			
			top:'0',
			right:'10px',
			orient: 'vertical'
		
		}, */
		xAxis: {
			
			axisLine:{// 设置X轴线条不要
				show: true
			},
			axisTick:{// 设置X轴刻度
				show: false
			},
			offset:5, // 设置X轴上数据与线条X轴的位置距离
			axisLabel: { //坐标轴刻度标签的相关设置
			//interval: 1, //横轴信息全部显示
				show: true,
				textStyle: {
					color: '#868B91',
				},
				fontSize: 14,//字体大小
			},
			splitLine: {  //  设置X轴线条为虚线
				show: true,
				lineStyle: {
					type: 'dashed',
					color:['#d1e0ee']  ,// 设置X轴虚线颜色,
					
			    }
			},
			data: ["5/4-10","5/11-17", "5/18-24", "5/25-31"],
			type: 'category',
			boundaryGap: false
			
		},
		yAxis: {
			axisLine:{// 设置X轴线条不要
				show: true
			},
			axisTick:{// 设置Y轴刻度
				show: false
			},
			offset:5, // 设置X轴上数据与线条X轴的位置距离
			axisLabel: { //坐标轴刻度标签的相关设置
			//interval: 1, //横轴信息全部显示
				show: true,
				textStyle: {
					color: '#868B91',
				},
				fontSize: 14,//字体大小
			},
			splitLine: {  //  设置X轴线条为虚线
				show: true,
				lineStyle: {
					type: 'dashed',
					color:['#d1e0ee']  ,// 设置X轴虚线颜色,
					
			    }
			},
			type: 'value',
			        axisLabel: {
			            formatter: '{value}'
			        },
			        axisPointer: {
			            snap: true
			        }
			
		},
		 visualMap: {
		        show: false,
		        dimension: 0,
		        pieces: [{
		            lte: 6,
		            color: '#3aa0ff'
		        }, {
		            gt: 6,
		            lte: 60,
		            color: '#3aa0ff'
		        }/* , {
		            gt: 8,
		            lte: 14,
		            color: 'green'
		        }, {
		            gt: 14,
		            lte: 17,
		            color: 'red'
		        }, {
		            gt: 60,
		            color: 'green'
		        } */]
		    },
		
		series: [
			{
				name: '每日标准 (单位：克)',
				/* '#3aa0ff' */
				type: 'line',				
				symbolSize:12,
				data: [30,51,87,46,105],
				itemStyle: {
					normal:{
						borderWidth:5, // 拐点边框大小
						borderColor:'#3aa0ff',  //拐点边框颜色
						lineStyle:{
							width:4, // 线条粗细

							
						}
					}
					
				},
				 markArea: {
						data: [ 
							[
								{
								name: '早高峰',
								xAxis: '30'
							}, {
								xAxis: '50'
							},
						]
						/* , [{
							name: '晚高峰',
							xAxis: '17:30'
						}, {
							xAxis: '21:15'
						}] */
						]
					}
				//  areaStyle: {}
			}
			
		]
	};
	
	// myChart1.setOption(option1);
	
	var option0 = {
	    title: {
	       /* text: '一天用电量分布',
	        subtext: '纯属虚构' */
	    },
	    tooltip: {
	        trigger: 'axis',
	        axisPointer: {
	            type: 'cross'
	        }
	    },
	    toolbox: {
	        show: false,
	        feature: {
	            saveAsImage: {}
	        },
			color:'#333',
			backgroundColor:'#fff',
			boxShadow:'0px 2px 20px 0px rgba(46,73,99,0.2)',
			borderColor:'#ccc'
	    }, 
	    xAxis: {
			axisLine:{// 设置X轴线条不要
				show: true,
				lineStyle:{
					color:'#d1e0ee',
					// width:8,//这里是为了突出显示加上的
				}

			},
			axisTick:{// 设置X轴线条不要
				show: false
			},
			offset:5, // 设置Y轴上数据与线条X轴的位置距离
			axisLabel: {  //坐标轴刻度标签的相关设置
				// interval: 1, //横轴信息全部显示
				show: true,
				textStyle: {
					color: '#868B91',
				},
				fontSize: 14,//字体大小
			},
	        type: 'category',
	        boundaryGap: false,
	       //  data: ['00:00', '01:15', '02:30', '03:45', '05:00', '06:15', '07:30', '08:45', '10:00', '11:15', '12:30', '13:45', '15:00', '16:15', '17:30', '18:45', '20:00', '21:15', '22:30', '23:45']
		   data: ["5/4-10","5/11-17", "5/18-24", "5/25-31"],
		   /* splitLine: {  //  设置X轴线条为虚线
		   	show: true,
		   	lineStyle: {
		   		type: 'dashed',
		   		color:['#d1e0ee']  ,// 设置X轴虚线颜色,
		   		
		       }
		   }, */
		   
	    },
	    yAxis: {
			axisLine:{// 设置X轴线条不要
				show: true,
				lineStyle:{
					color:'#d1e0ee',
					// width:8,//这里是为了突出显示加上的
				}
			},
			axisTick:{// 设置X轴线条不要
				show: false
			},
	        type: 'value',
	        offset:5, // 设置Y轴上数据与线条X轴的位置距离
	        axisLabel: {  //坐标轴刻度标签的相关设置
	        	// interval: 1, //横轴信息全部显示
	        	show: true,
	        	textStyle: {
	        		color: '#868B91',
	        	},
	        	fontSize: 14,//字体大小
	        },
	        axisPointer: {
	            snap: true
	        },
			splitLine: {  //  设置X轴线条为虚线
				show: true,
				lineStyle: {
					type: 'dashed',
					color:['#d1e0ee']  ,// 设置X轴虚线颜色,
					
			    }
			},
	    },
	    visualMap: {
	        show: false,
	        dimension: 0,
	        pieces: [{
	            lte: 6,
	            color: '#3aa0ff'
	        }, {
	            gt: 6,
	            lte: 8,
	            color: '#3aa0ff'
	        }, {
	            gt: 8,
	            lte: 14,
	            color: '#3aa0ff'
	        }, {
	            gt: 14,
	            lte: 17,
	            color: '#3aa0ff'
	        }, {
	            gt: 17,
	            color: '#3aa0ff'
	        }]
	    },
	    series: [
	        {
	            name: '摄入盐分相较 前一周下降',
	            type: 'line',
	            smooth: true,
	           //  data: [300, 280, 250, 260, 270, 300, 550, 500, 400, 390, 380, 390, 400, 500, 600, 750, 800, 700, 600, 400],
			   data: [30,51,87,46,105],
	            markArea: {
	                data: [
						[
							{
							  //  name: '早高峰',
								xAxis: '5/18-24'
							},
							{
								xAxis: '5/25-31'
							},
						],
					/* , [{
	                   // name: '晚高峰',
	                    xAxis: '17:30'
	                }, {
	                    xAxis: '21:15'
	                }] */ 
					],
					itemStyle:{
						color:['#ecf3fb'],
						opacity:0.8,
						border:'5px solid #51a9ff'
					}
	            },
				itemStyle: {
					normal:{
						 borderWidth:5, // 拐点边框大小
						borderColor:'#3aa0ff',  //拐点边框颜色
						lineStyle:{
							width:4, // 线条粗细
							shadowColor:'rgba(81,169,255,0.4)',

							
						}
					}
					
				}
	        }
	    ]
	};

	myChart1.setOption(option0);
	
	
	// 柱状体
	var myChart2 = echarts.init(document.getElementById('main2'));

	// 指定图表的配置项和数据
	var option2 = {
		grid: { //配置canves图标父元素div的距离(就是canves与周边距离)
			top:"25px",
		    left:"10px",
		    right:"10px",
		    bottom:"20px",
			width:'auto',
			containLabel: true
		},
		title: {
			/* text: '*最大摄入量每日8克，日均摄入6克',
			textStyle: {
				fontSize: 10,
				fontWeight: 'normal',
				color:'#868c92'
			},
			height:5 */

		},
		tooltip: {},
		color:'#32ff22',
		legend: {
			 data: [ '每日标准 (单位：克)' ],
			textStyle:{
				fontSize: 10,
				fontWeight: 'normal',
				color:'#868c92',				
			},
			itemWidth:20,			
			itemHeight:5,			
			top:'0',
			right:'10px',
			orient: 'vertical'

		},
		xAxis: {
			axisLine:{// 设置X轴线条不要
				show: false
			},
			axisTick:{// 设置X轴线条不要
				show: false
			},
			
			data: ["5/24","5/25", "5/26", "5/27", "5/28", "5/29", "5/30"],
			offset:5, // 设置X轴上数据与线条X轴的位置距离
			axisLabel: { //坐标轴刻度标签的相关设置
			//interval: 1, //横轴信息全部显示
				show: true,
				textStyle: {
					color: '#868B91',
				},
				fontSize: 14,//字体大小
			}
		},
		yAxis: {
			axisLine:{ // 设置Y轴线条不要
				show:false
			},
			 axisTick:{ // 设置Y轴线条不要
				show:false
			},
			splitLine: {  //  设置X轴线条为虚线
				show: true,
				lineStyle: {
					type: 'dashed',
					color:['#b1b6bc','#b1b6bc','#b1b6bc','#32ff22','#b1b6bc']  ,// 设置X轴虚线颜色,
					
			    }
			},
			offset:5, // 设置Y轴上数据与线条X轴的位置距离
			axisLabel: {  //坐标轴刻度标签的相关设置
				// interval: 1, //横轴信息全部显示
				show: true,
				textStyle: {
					color: '#868B91',
				},
				fontSize: 14,//字体大小
			}
		},
		series: [
			{
				name: ' 每日标准 (单位：克) ',
				type: 'bar',
				barWidth: '20%',
				
				data: [7, 6.1, 8.2, 7.6, 9, 7,10],
				itemStyle: {
					normal:{
						barBorderRadius: [8],
						 color: '#3aa0ff',
						 type:'dashed',
						 label: { //柱顶部显示数值
						 	show: true,
						 	position: 'top',
						 	distance: 3,
						 	textStyle: {	    //数值样式
						 	    fontSize: 14,
								color:'#868B91',
						 	},
						 	formatter: function (params) { //数据为0时不显示数字0
						 		if (params.value > 0) {
						 			return params.value;
						 		} else {
						 			return '';
						 		}
						     }
						 }
					}	
					
				}
			}
			
		]
	};

	// 使用刚指定的配置项和数据显示图表。
	myChart2.setOption(option2);
	
	
	// 切换
	$('.sort span').click(function(){
		$(this).addClass('active').siblings('span').removeClass('active');
	})
	
})
