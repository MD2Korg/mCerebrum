import React from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, List, ListItem} from 'native-base';
import {YellowBox, StyleSheet,TouchableOpacity, ScrollView, NativeModules, FlatList, Alert} from 'react-native';
import { Switch } from 'react-native-switch';
import Row from '../../components/Row';
const timer = require('react-native-timer');
import * as Animatable from 'react-native-animatable';
import Collapsible from 'react-native-collapsible';
import Accordion from 'react-native-collapsible/Accordion';
YellowBox.ignoreWarnings(['Warning: isMounted(...) is deprecated', 'Module RCTImageLoader']);


class Home extends React.Component {
  state = {
    switch1Value: false,
    data_collection_background_color:'red',
    data_collection_text: 'Data collection: OFF',
    content: [
          { title: 'Please configure the plugins, start the study and refresh the screen'},
        ],
        content1: [
          { title: 'Please configure the plugins, start the study and refresh the screen'},
            ],
            activeSection: false,
            collapsed: true,
            active: false,

    uniqueValue: 1,
  };
  constructor(props) {
    super(props);
    this.props.navigator.setTitle({title: 'mCerebrum'});
    this.props.navigator.setOnNavigatorEvent(this.onNavigatorEvent.bind(this));
    NativeModules.ActivityStarter.isDataCollection(result=>{
      this.state.switch1Value=result;
      if(result==true){
        this.state.data_collection_text='Data collection: ON';
        this.state.data_collection_background_color='green';

      }
        else{
        this.state.data_collection_text='Data collection: OFF';
        this.state.data_collection_background_color='red';
      }
      {this.forceRemount()};
    });

  }
  componentWillUnmount() {
    console.log('......................home unmount.........................')
//      timer.clearInterval(this);
    }
    componentDidFocus(){
      console.log('......................home focus.........................')

    }

  componentDidMount() {
    console.log('......................home mount.........................')
    {this.getDataSourceInfos()}
//    timer.setInterval(this, 'hideMsg',()=>this.getDataSourceInfos() , 5000);
  }
  getDataSourceInfos=()=>{
    NativeModules.ActivityStarter.getDataSourceInfo(result=>{
      this.state.content = JSON.parse(result);
      if(this.state.content==null || this.state.content.length==0)
         this.state.content = this.state.content1;
      {this.forceRemount()};
    });
  }
  forceRemount = () => {
    this.setState({
      uniqueValue: this.state.uniqueValue + 1
    });
  }
  toggleSwitch1 = (value) => {
    this.setState({switch1Value: value})
    if(value==true){
      this.setState({data_collection_text: 'Data Collection: ON'});
      this.setState({data_collection_background_color: 'green'});
      NativeModules.ActivityStarter.dataCollection(true);
    }
    else{
      this.setState({data_collection_text: 'Data Collection: OFF'});
      this.setState({data_collection_background_color: 'red'});
      NativeModules.ActivityStarter.dataCollection(false);
      console.log('Switch 1 is: ' + value)
    }
  }
  onNavigatorEvent(event) {
    if (event.type === 'DeepLink') {
      const parts = event.link.split('/');
      if (parts[0] === 'tab1') {
        this.props.navigator.push({
          screen: parts[1]
        });
      }
    }
    switch(event.id) {
      case 'didAppear':
      case 'onActivityResumed':
      {this.getDataSourceInfos()}
      break;

    }

  }
  _toggleExpanded = () => {
    this.setState({ collapsed: !this.state.collapsed });
  };

  _setSection(section) {
    this.setState({ activeSection: section });
  }
  _setSectionCC(section) {
    this.setState({ activeSectionCC: section });
  }

  _renderHeader=(section, i, isActive) => {
    return (
      <Animatable.View
      duration={400}
      style={{backgroundColor: '#131325'}}
      transition="backgroundColor"
      >
      <Card style={{backgroundColor: '#131325'}}>
      <CardItem bordered style={{backgroundColor: '#1e2c3c'}}>
      <Left>
      <Text style={{color: 'white'}}>{section.title}</Text>
      </Left>
      <Right>
      <Icon name="ios-arrow-down-outline"/>
      </Right>
      </CardItem>
      </Card>
      </Animatable.View>

    );
  }
  _renderContent(section, i, isActive) {
    plot=(item)=>{
      if(item.plot==true)
        NativeModules.ActivityStarter.plot(item.dataSourceType, item.dataSourceId, item.platformType, item.platformId, item.applicationType, item.applicationId);
    }

    return (
      <Animatable.View
      duration={400}
      style={[styles.content, isActive ? styles.active : styles.inactive]}
      transition="backgroundColor"
      >
      <List dataArray={section.dataSources}
      renderRow={(item) =>
        <ListItem button onPress={()=>{plot(item)}}>
        <Left>
        <Text note style={{color: 'white'}}>{item.title}</Text>
        </Left>
        <Body>
        <Text  note style={{color: 'white'}}># {item.sampleNo}</Text>
        </Body>
        <Right>
        {item.plot &&
        <Icon name='bar-graph' type='Entypo'></Icon>
      }
        </Right>
        </ListItem>
      }>
      </List>
      </Animatable.View>
    );
  }

  render() {
    return (
      <Container style={{backgroundColor: '#131325'}} key={this.state.uniqueValue}>
      <Content>
      <Card style={{backgroundColor: '#1e2c3c'}}>
      <CardItem header  style={{ backgroundColor: this.state.data_collection_background_color}}>

      <Left>
      <Text style={{color: 'white'}}>{this.state.data_collection_text}</Text>
      </Left>
      <Right>
      <Switch onValueChange = {this.toggleSwitch1}
      value = {this.state.switch1Value}
      activeText={'On'}
      inActiveText={'Off'}
      disabled={false}
      backgroundActive={'gray'}
      backgroundInactive={'gray'}
      circleActiveColor={'white'}
      circleInActiveColor={'white'}       />
      </Right>
      </CardItem>
      </Card>
      <Card style={{backgroundColor: '#1e2c3c'}}>
      <CardItem header style={{ backgroundColor: '#00838F'}}>
      <Left>
      <Text style={{color: 'white'}}>Data Stream Summary</Text>
      </Left>
      <Right>
      <Button transparent onPress={() => {this.getDataSourceInfos()}}>
      <Icon name='md-refresh' style={{fontSize: 32, color: 'white'}}/>
      </Button>
      </Right>
      </CardItem>
      </Card>

      <Accordion
      activeSection={this.state.activeSection}
      sections={this.state.content}
      touchableComponent={TouchableOpacity}
      renderHeader={this._renderHeader}
      renderContent={this._renderContent}
      duration={400}
      onChange={this._setSection.bind(this)}
      />
      </Content>
      </Container>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  row: {
    height: 48,
    paddingHorizontal: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(0, 0, 0, 0.054)'
  },
  text: {
    fontSize: 16,
  },
});

export default Home;
