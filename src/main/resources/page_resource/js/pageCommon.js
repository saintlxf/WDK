/*
    页面常用函数
 */
/*
    清除form中input的内容
 */
var clearFormInputContent = function (form) {
    form.find("input").each(function () {
        $(this).val('');
    });
}