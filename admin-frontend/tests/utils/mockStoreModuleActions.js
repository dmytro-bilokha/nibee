
import sinon from 'sinon';

const mockStoreModuleActions = (storeModule) => {
  const mockActions = {};
  for (let action in storeModule.actions) {
    mockActions[action] = sinon.fake();
  }
  return { ...storeModule
         , actions: mockActions
         };
};

export default mockStoreModuleActions;
