'use strict'

const path = require('path')
const devEnv = require('./dev.env')

module.exports = {
  dev: {

    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    // 代理列表, 是否开启代理通过[./dev.env.js]配置
    proxyTable: devEnv.OPEN_PROXY === false ? {} : {
      '/proxyApi': {
        target: 'http://demo.renren.io/renren-fast/',
        changeOrigin: true,
        pathRewrite: {
          '^/proxyApi': '/'
        }
      }
    },

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 8001, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: true,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-


    useEslint: true,

    showEslintErrorsInOverlay: false,

    devtool: 'eval-source-map',

    cacheBusting: true,

    cssSourceMap: false,
  },

  build: {

    index: path.resolve(__dirname, '../dist/index.html'),


    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: './',

    productionSourceMap: false,
    devtool: '#source-map',
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],
    bundleAnalyzerReport: process.env.npm_config_report
  }
}
