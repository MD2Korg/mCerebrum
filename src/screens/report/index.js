import React, {Component} from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, Toast} from 'native-base';

import { StyleSheet,  View, TouchableOpacity, Image, Alert, AppRegistry, NativeModules, ToastAndroid } from 'react-native';
import * as Animatable from 'react-native-animatable';
import Collapsible from 'react-native-collapsible';
import Accordion from 'react-native-collapsible/Accordion';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#F5FCFF'
  },
  title: {
    textAlign: 'center',
    fontSize: 22,
    fontWeight: '300',
    marginBottom: 20
  },
  header: {
    backgroundColor: '#F5FCFF',
    padding: 10
  },
  headerText: {
    textAlign: 'left',
    fontSize: 16,
    fontWeight: '500'
  },
  content: {
    padding: 20,
    backgroundColor: '#fff'
  },
  active: {
    backgroundColor: 'rgba(255,255,255,1)'
  },
  inactive: {
    backgroundColor: 'rgba(245,252,255,1)'
  },
  selectors: {
    marginBottom: 10,
    flexDirection: 'row',
    justifyContent: 'center'
  },
  selector: {
    backgroundColor: '#F5FCFF',
    padding: 10
  },
  activeSelector: {
    fontWeight: 'bold'
  },
  selectTitle: {
    fontSize: 14,
    fontWeight: '500',
    padding: 10
  },
  card_button_text: {
    padding: 0,
    fontSize: 10,
  },
});

class Report extends React.Component {

  state = {
    activeSection: false,
    collapsed: true,
    active: false,
    uniqueValue: 1,
    content: [],
  };
  constructor(props){
    super(props);

    this.props.navigator.setTitle({title: 'mCerebrum : Activities'});
    this.props.navigator.setButtons({
      rightButtons: [{
        id: 'addMarker',
        icon: require('../../../img/edit.png'),
      }],
      animated: true
    });
    this.props.navigator.addOnNavigatorEvent(this.onNavigatorEvent.bind(this));
    {this.readMarker()};

  }
  onNavigatorEvent(event) {
    console.log('event='+event.id);
    switch(event.id) {
      case 'addMarker':
      this.showModal();
      break;
      case 'didAppear':
      this.readMarker();

    }
  }
  showModal = () => {
    this.props.navigator.showModal({
      screen: 'mc.Report.MarkerEdit',
      title: 'Modal',
    });
  };
  readMarker=()=>{
    NativeModules.ActivityStarter.readMarker(result=>{
      this.state.content = JSON.parse(result);
      this.setState({
        uniqueValue: this.state.uniqueValue + 1
      });
    });
  }
  shouldComponentUpdate(newProps, newState) {
    return this.state.uniqueValue!=newState.uniqueValue;
  }
  componentWillUpdate(nextProps, nextState) {
  }
  showToast=(title, button)=>{
    ToastAndroid.show(title+" -> "+button, ToastAndroid.SHORT);
    NativeModules.ActivityStarter.setMarker(title, button);
  }

  render() {
    return (
      <Container style={{backgroundColor: '#131325'}} key={this.state.uniqueValue}>
      <Content>
      {this.state.content.map((item, index) => {
        return(
          <Card key={index} >
          <CardItem bordered style={{backgroundColor: '#1e2c3c'}} >
          <Left>
          <Text style={{color: 'white'}}>{item.title}</Text>
          </Left>
          <Body>
          <Button warning bordered onPress={()=> {this.showToast(item.title,item.button1)}}><Text> {item.button1} </Text></Button>
          </Body>
          <Right>
          {item.button2!=='' &&

          <Button warning bordered onPress={()=> {this.showToast(item.title,item.button2)}}><Text> {item.button2} </Text></Button>
        }
        </Right>

        </CardItem>
        </Card>
      );
    })}
    </Content>
    </Container>                );
  }
}

export default Report;
