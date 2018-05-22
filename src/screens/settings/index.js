import React, { Component } from 'react';

import { AppRegistry, StyleSheet, FlatList, Text, View, Alert, Platform, Image } from 'react-native';
import { Container, Header, Content, Icon } from 'native-base';

class Settings extends React.Component {
 constructor(props)
 {
   super(props);
   this.props.navigator.setTitle({title: 'mCerebrum'});

   this.state = { GridViewItems: [
     {key: 'study', title: 'Study',icon: 'test-tube', icon_type: 'MaterialCommunityIcons'},
     {key: 'storage', title: 'Storage',icon:'sd-storage', icon_type:'MaterialIcons'},
     {key: 'cc', title: 'Cerebral Cortex',icon: 'md-cloud-outline',icon_type: 'Ionicons'}
     // {key: 'Plugins'},
     // {key: 'Study'},
     // {key: 'Sensors/Markers'},
     // {key: 'Sixteen'},
     // {key: 'Seventeen'},
     // {key: 'Eighteen'},
     // {key: 'Nineteen'},
     // {key: 'Twenty'}
   ]}
 }
   pushScreen = () => {
     this.props.navigator.showModal({
       title: 'Study',
       screen: 'mc.Settings.Study.Login',
     });
   };

 GetGridViewItem (item) {
   if(item==='study')
    this.pushScreen();
   else
      Alert.alert(item);
 }


 render() {
   return (

<View style={styles.MainContainer}>

      <FlatList

         data={ this.state.GridViewItems }

         renderItem={({item}) =><View style={styles.GridViewBlockStyle}>
<Icon type={item.icon_type} name={item.icon} style={{fontSize: 40, color: '#80DEEA', active: false}} onPress={this.GetGridViewItem.bind(this, item.key)}/>
            <Text style={styles.GridViewInsideTextItemStyle} onPress={this.GetGridViewItem.bind(this, item.key)} > {item.title} </Text>
            </View>
          }

         numColumns={3}

        />


</View>

   );
 }
}

const styles = StyleSheet.create({

MainContainer :{

justifyContent: 'center',
flex:1,
paddingTop: (Platform.OS) === 'ios' ? 20 : 0,
backgroundColor: '#131325'
},

GridViewBlockStyle: {

  justifyContent: 'center',
  flex:2,
  alignItems: 'center',
  height: 100,
  margin: 1,
  backgroundColor: '#131325'

}
,

GridViewInsideTextItemStyle: {

   color: '#80DEEA',
   padding: 4,
   fontSize: 12,
   justifyContent: 'center',

 },

});

export default Settings;
