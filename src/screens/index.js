import {Navigation, ScreenVisibilityListener} from 'react-native-navigation';

import Home from './home/index';
import About from './about/index';
import Settings from './settings/index';
import Report from './report/index';

export function registerScreens() {
  Navigation.registerComponent('mc.Home', () => Home);
  Navigation.registerComponent('mc.About', () => About);
  Navigation.registerComponent('mc.Settings', () => Settings);
  Navigation.registerComponent('mc.Report', () => Report);

}

export function registerScreenVisibilityListener() {
  new ScreenVisibilityListener({
    willAppear: ({screen}) => console.log(`Displaying screen ${screen}`),
    didAppear: ({screen, startTime, endTime, commandType}) => console.log('screenVisibility', `Screen ${screen} displayed in ${endTime - startTime} millis [${commandType}]`),
    willDisappear: ({screen}) => console.log(`Screen will disappear ${screen}`),
    didDisappear: ({screen}) => console.log(`Screen disappeared ${screen}`)
  }).register();
}
