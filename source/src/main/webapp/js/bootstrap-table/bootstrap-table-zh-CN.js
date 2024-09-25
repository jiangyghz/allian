/**
 * Bootstrap Table Chinese translation
 * Author: Zhixin Wen<wenzhixin2010@gmail.com>
 */
(function ($) {
    'use strict';

    $.fn.bootstrapTable.locales['zh-CN'] = {
        formatLoadingMessage: function () {
            return '正在努力地加载数据中，请稍候……';
        },
        formatRecordsPerPage: function (pageNumber) {
            return '每页显示 ' + pageNumber + ' 条记录';
        },
        formatShowingRows: function (pageFrom, pageTo, totalRows) {
            return '显示第 ' + pageFrom + ' 到第 ' + pageTo + ' 条记录，总共 ' + totalRows + ' 条记录';
        },
        formatSearch: function () {
            return '搜索';
        },
        formatNoMatches: function () {
            return '没有找到匹配的记录';
        },
        formatPaginationSwitch: function () {
            return '隐藏/显示分页';
        },
        formatRefresh: function () {
            return '刷新';
        },
        formatToggle: function () {
            return '切换';
        },
        formatColumns: function () {
            return '列';
        },
        formatExport: function () {
            return '导出数据';
        },
        formatClearFilters: function () {
            return '清空过滤';
        }


    };
    $.fn.bootstrapTable.defaults.sidePagination="server";
    $.fn.bootstrapTable.defaults.showToggle=false;
    $.fn.bootstrapTable.defaults.search=false;
    $.fn.bootstrapTable.defaults.pageList=[10, 30, 50, 100];
    $.fn.bootstrapTable.columnDefaults.align="left";
    $.fn.bootstrapTable.columnDefaults.falign="left";
    $.fn.bootstrapTable.columnDefaults.halign="left";
    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);

})(jQuery);
