import React, {Component} from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, Form, Item, Label, Input} from 'native-base';

import { StyleSheet,  View, TouchableOpacity, Image, Alert, AppRegistry, NativeModules } from 'react-native';
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

class MarkerEdit extends React.Component {

  state = {
    activeSection: false,
    collapsed: true,
    active: false,
    content: [],
    uniqueValue: 1,
    title: '',
    button1: '',
    button2: '',
  };
  constructor(props){
    super(props);
    this.props.navigator.setTitle({title: 'Edit Markers'});
    {this.readMarkers()};

  }
  componentDidMount() {
    {this.readMarkers()};
  }
  readMarkers=()=>{
    NativeModules.ActivityStarter.readMarker(result=>{
      this.state.content = JSON.parse(result);
      {this.forceRemount()};
    });
  }
  forceRemount = () => {
    this.setState({
      uniqueValue: this.state.uniqueValue + 1
    });
  }
  addMarker=()=>{
    if(this.state.title=='' || this.state.button1==''){
      Alert.alert('Title and/or button1 cannot be empty');
    }else{
      NativeModules.ActivityStarter.addMarker(this.state.title,this.state.button1,this.state.button2, result=>{
        this.state.content = JSON.parse(result);
        {this.forceRemount()};
      });
    }
  }
  deleteMarker=(title)=>{
    NativeModules.ActivityStarter.deleteMarker(title, result=>{
      this.state.content = JSON.parse(result);
      {this.forceRemount()};
    });
  }

  render() {
    return (
      <Container style={{backgroundColor: '#131325'}}  key={this.state.uniqueValue}>
      <Content>
      <Text  style={{ backgroundColor: '#00838F', color: 'white', fontSize: 16, textAlign: 'center', lineHeight: 50}}>Add Markers</Text>
      <Form style={{backgroundColor: '#1e2c3c'}}>
      <Item stackedLabel>
      <Label style={{color: 'white'}}>Title</Label>
      <Input style={{color: 'white'}}   onChangeText={(title) => this.setState({title})}/>
      </Item>
      <Item stackedLabel>
      <Label style={{color: 'white'}}>Button1 Text</Label>
      <Input style={{color: 'white'}}    onChangeText={(button1) => this.setState({button1})}/>
      </Item>
      <Item stackedLabel>
      <Label style={{color: 'white'}}>Button2 Text (Optional)</Label>
      <Input style={{color: 'white'}}    onChangeText={(button2) => this.setState({button2})}/>
      </Item>
      </Form>
      <Card>
      <CardItem style={{backgroundColor: '#1e2c3c'}}>
      <Left/>
      <Body>
      <Button success  onPress={() => this.addMarker()}>
      <Icon name='add-circle-outline' type='MaterialIcons'/>
      <Text>Add</Text>
      </Button>
      </Body>
      <Right/>
      </CardItem>
      </Card>
      <Text  style={{ backgroundColor: '#00838F', color: 'white', fontSize: 16, textAlign: 'center', lineHeight: 50}}>Remove Markers</Text>

      {this.state.content.map((item, index) => {
        return(
          <Card key={index} >
          <CardItem bordered style={{backgroundColor: '#1e2c3c'}}>
          <Left>
          <Text style={{color: 'white'}}>{item.title}</Text>
          </Left>
          <Right>
          <Button warning bordered onPress={() => this.deleteMarker(item.title)}>
          <Icon name='delete' type='MaterialIcons'/>
          </Button>
          </Right>

          </CardItem>
          </Card>
        );
      })}
      </Content>
      </Container>                );
    }
  }

  export default MarkerEdit;
