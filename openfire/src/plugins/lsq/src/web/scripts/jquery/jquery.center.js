(function($){ 
$.fn.center = function(){
var top = ($(window).height() - this.height())/2;
var left = ($(window).width() - this.width())/2;
var scrollTop = $(document).scrollTop();
var scrollLeft = $(document).scrollLeft();
return this.css( { position : 'absolute', 'top' : top + scrollTop, left : left + scrollLeft } ).show();
}
})(jQuery)