module.exports = {
  root: true
  , env: {
    node: true
  }
  , 'extends': [
    'plugin:vue/essential'
    , '@vue/standard'
  ]
  , rules: {
    'no-console': 'off'
    , 'comma-style': ['error', 'first']
    , 'operator-linebreak': ['error', 'before', { 'overrides': { '=': 'after' } }]
    , 'space-before-function-paren': ['error', 'never']
    , 'spaced-comment': ['error', 'never']
    , 'indent': ['error', 2
      , { 'ImportDeclaration': 'first'
        , 'ObjectExpression': 'first'
        , 'ArrayExpression': 'first'
      }]
    , 'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
    , 'semi': ['error', 'always']
  }
  , parserOptions: {
    parser: 'babel-eslint'
  }
};
