requirejs.config({

    baseUrl: 'assets',

    paths:{
        jquery: 'javascripts/jquery-1.8.3.min',
        bootstrap:'javascripts/bootstrap-2.2.2.min'
    }

});

require(['jquery', 'bootstrap']);