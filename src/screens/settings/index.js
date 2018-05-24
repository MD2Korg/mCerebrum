import React, { Component } from 'react';

import { AppRegistry, StyleSheet, FlatList, View, Alert, Platform, Image, NativeModules } from 'react-native';
import { Container, Header, Content, List, ListItem, Text, Icon, Left, Body, Right, Switch } from 'native-base';

class Settings extends React.Component {
 constructor(props)
 {
   super(props);
   this.props.navigator.setTitle({title: 'mCerebrum : Settings'});
 }
   render() {
       return (
         <Container style={{backgroundColor: '#131325'}}>
           <Content>
             <List>
             <ListItem itemDivider style={{ backgroundColor: '#00838F'}}>
                           <Text style={{ color: 'white'}}>Storage</Text>
                         </ListItem>
            <ListItem style={{ backgroundColor: '#1e2c3c'}} onPress={() => NativeModules.ActivityStarter.clearData()}>
                 <Body>
                   <Text style={{color: 'white'}}>Clear Data</Text>
                 </Body>
                 <Right>
                 <Icon name="arrow-forward" />
                 </Right>
               </ListItem>
             </List>
           </Content>
         </Container>
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

});

export default Settings;
