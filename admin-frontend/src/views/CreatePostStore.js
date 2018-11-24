
const state =
  { file: null
  , title: ''
  , webPath: ''
  , fsPath: ''
  , shareable: true
  , commentAllowed: true
  , tags: []
  };

const mutations = 
  { setFile(state, file) {
      state.file = file;
    }
  , setTitle(state, title) {
      state.title = title;
    }
  , setWebPath(state, webPath) {
      state.webPath = webPath;
    }
  , setFsPath(state, fsPath) {
      state.fsPath = fsPath;
    }
  , setShareable(state, shareable) {
      state.shareable = shareable;
    }
  , setCommentAllowed(state, commentAllowed) {
      state.commentAllowed = commentAllowed;
    }
  , setTags(state, tags) {
      state.tags = tags;
    }
  };

const actions = {};

const getters = 
  { file: state => state.file
  , title: state => state.title
  , webPath: state => state.webPath
  , fsPath: state => state.fsPath
  , shareable: state => state.shareable
  , commentAllowed: state => state.commentAllowed
  , tags: state => state.tags
  };

const createPostStore =
  { namespaced: true
  , state
  , mutations
  , actions
  , getters
  };

export default createPostStore;
