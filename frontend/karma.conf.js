module.exports = function (config) {
    config.set({
      basePath: '',
      frameworks: ['jasmine', '@angular-devkit/build-angular'],
      plugins: [
        require('karma-jasmine'),
        require('karma-chrome-launcher'),
        require('karma-jasmine-html-reporter'),
        require('karma-coverage-istanbul-reporter'),
        require('@angular-devkit/build-angular/plugins/karma')
      ],
      client: {
        jasmine: {
          // you can add configuration options for Jasmine here
        },
        clearContext: false // leave Jasmine Spec Runner output visible in browser
      },
      jasmineHtmlReporter: {
        suppressAll: true // removes the duplicated failures
      },
      coverageIstanbulReporter: {
        dir: require('path').join(__dirname, './coverage/sportApp'),
        reports: ['html', 'lcovonly', 'text-summary'],
        fixWebpackSourcePaths: true
      },
      reporters: ['progress', 'kjhtml', 'coverage-istanbul'],
      port: 9876,
      colors: true,
      logLevel: config.LOG_INFO,
      autoWatch: true,
      browsers: ['Chrome'],
      singleRun: false,
      restartOnFileChange: true
    });
  };
  