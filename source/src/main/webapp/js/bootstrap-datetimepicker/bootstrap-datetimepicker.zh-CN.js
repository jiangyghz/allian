/**
 * Simplified Chinese translation for bootstrap-datetimepicker
 * Yuan Cheung <advanimal@gmail.com>
 */
;(function($){
	$.fn.datetimepicker.dates['zh-CN'] = {
			days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
			daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日"],
			daysMin:  ["日", "一", "二", "三", "四", "五", "六", "日"],
			months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
			monthsShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
			today: "今天",
			suffix: [],
			meridiem: ["上午", "下午"]
	};
}(jQuery));
//默认
if($.fn.datetimepicker){
    $.fn.datetimepicker.defaults.bootcssVer=3;
    $.fn.datetimepicker.defaults.language="zh-CN";
    $.fn.datetimepicker.defaults.pickerPosition="bottom-left";
    $.fn.datetimepicker.defaults.weekStart=7;
    $.fn.datetimepicker.defaults.todayBtn=1;
    $.fn.datetimepicker.defaults.autoclose=true;
    $.fn.datetimepicker.defaults.todayHighlight=1;
    $.fn.datetimepicker.defaults.startView=2;
    $.fn.datetimepicker.defaults.minView=0; //2 month，1，houer,0 5min
    $.fn.datetimepicker.defaults.forceParse=0;
}
