function karateConfig() {
    var env = karate.env; // get java system property 'karate.env'
    karate.log('karate.env system property was:', env);
    if (!env) {
        env = 'dev'; // a custom 'intelligent' default
    }

    karate.log('env:', env);
    var config = { // base config JSON
        env: env
    };
    config = karate.callSingle('classpath:setup.feature', config);

    karate.configure('connectTimeout', 300000);
    karate.configure('readTimeout', 300000);

    karate.configure('logPrettyResponse', true);
    karate.configure('logPrettyRequest', true);

    return config;
}