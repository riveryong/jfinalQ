/**
 * 非AMDjs文件引入
 */
requirejs.config({
    paths:{
        jquery : 'http://cdn.staticfile.org/jquery/2.1.1-rc2/jquery.min',
        bootstrap : 'http://cdn.staticfile.org/twitter-bootstrap/3.2.0/js/bootstrap.min',
    },
    shim:{
	    bootstrap: {
            deps: ['jquery'],
            exports:'bs'
        }
    }
});