import sinon from 'sinon';

export const mockStoreModuleActions = (storeModule) => {
  const mockActions = {};
  for (let action in storeModule.actions) {
    mockActions[action] = sinon.fake();
  }
  return { ...storeModule
         , actions: mockActions
         };
};

export const getTagsMock = () => {
  return [ { id: 1
           , name: 'FreeBSD'
           }
         , { id: 2
           , name: 'Linux'
           }
         , { id: 3
           , name: 'Windows'
           }
         ];
};
