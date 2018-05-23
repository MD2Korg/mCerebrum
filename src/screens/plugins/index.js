import React, {Component} from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title} from 'native-base';

import { StyleSheet,  View, TouchableOpacity, Image, Alert, AppRegistry, NativeModules } from 'react-native';
import * as Animatable from 'react-native-animatable';
import Collapsible from 'react-native-collapsible';
import Accordion from 'react-native-collapsible/Accordion';
const BACON_IPSUM =
  'Bacon ipsum dolor amet chuck turducken landjaeger tongue spare ribs. Picanha beef prosciutto meatball turkey shoulder shank salami cupim doner jowl pork belly cow. Chicken shankle rump swine tail frankfurter meatloaf ground round flank ham hock tongue shank andouille boudin brisket. ';
  const CONTENT = [
    {
      title: 'Phone Sensor',
      summary: 'Captures phone sensor data',
      pre_requisite: '',
      tag: ['phon-accelerometer','phone-gyroscope','phone-compass','activity','gps','location'],
      current_version:'3.0.0',
      latest_version: '3.0.1',
      content: BACON_IPSUM
    },
    {
      title: 'Motion Sense',
      summary: 'Captures motion sense wrist sensor data',
      content: BACON_IPSUM
    },
    {
      title: 'AutoSense',
      summary: 'Captures autosense chest sensor data',
      content: BACON_IPSUM
    },
    {
      title: 'EasySense',
      summary: 'Captures easysense sensor data',
      content: BACON_IPSUM
    },
    {
      title: 'Stream Processor',
      summary: 'Infer stress and smoking events',
      content: BACON_IPSUM
    }
  ];
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
    }
  });

class Plugins extends React.Component {

  state = {
      activeSection: false,
      collapsed: true,
      active: false
    };

    _toggleExpanded = () => {
      this.setState({ collapsed: !this.state.collapsed });
    };

    _setSection(section) {
      this.setState({ activeSection: section });
    }
    _renderHeader=(section, i, isActive) => {
      return (
        <Animatable.View
          duration={400}
          style={[styles.header, isActive ? styles.active : styles.inactive]}
          transition="backgroundColor"
        >
        <Card>
                    <CardItem>
                      <Left>
                        <Body>
                          <Text >{section.title}</Text>
                          <Text note>{section.summary}</Text>
                        </Body>
                      </Left>
                    </CardItem>
                    <CardItem>
                                  <Left>
                                    <Button transparent >
                                    <Icon active name="ios-add-circle-outline" type="Ionicons"/>
                                      <Text>Add</Text>
                                    </Button>
                                  </Left>
                                  <Body>
                                    <Button transparent onPress={() =>alert('Add payment')}>
                                      <Icon active name="ios-remove-circle-outline" type="Ionicons"/>
                                      <Text>Remove</Text>
                                    </Button>
                                  </Body>
                                  <Right>
                                  <Button transparent onPress={() => NativeModules.ActivityStarter.navigateToExample()}>
                                    <Icon active name="ios-settings-outline" type="Ionicons"/>
                                    <Text>Settings</Text>
                                  </Button>
                                  </Right>
                                </CardItem>
                                                  </Card>
                          </Animatable.View>

      );
    }

    _renderContent(section, i, isActive) {
      return (
        <Animatable.View
          duration={400}
          style={[styles.content, isActive ? styles.active : styles.inactive]}
          transition="backgroundColor"
        >
          <Animatable.Text animation={isActive ? 'slideInDown' : undefined}>
            {section.content}
          </Animatable.Text>
        </Animatable.View>
      );
    }
    constructor(props){
      super(props);
      this.props.navigator.setTitle({title: 'mCerebrum'});
    }

    render() {
      return (
        <Container>
        <Header>
                  <Left/>
                  <Body>
                    <Title>List of Plugins</Title>
                  </Body>
                  <Right />
                </Header>
                <Content>
 <Accordion
            activeSection={this.state.activeSection}
            sections={CONTENT}
            touchableComponent={TouchableOpacity}
            renderHeader={this._renderHeader}
            renderContent={this._renderContent}
            duration={400}
            onChange={this._setSection.bind(this)}
          />
          </Content>
                    <Fab
                      active={this.state.active}
                      direction="up"
                      containerStyle={{ }}
                      style={{ backgroundColor: '#5067FF' }}
                      position="bottomRight"
                      onPress={() => this.setState({ active: !this.state.active })}>
                      <Icon name="share" />
                      <Button style={{ backgroundColor: '#34A34F' }}>
                        <Icon name="qrcode-scan" type="MaterialCommunityIcons"/>
                      </Button>
                      <Button style={{ backgroundColor: '#3B5998' }}>
                        <Icon name="http" type="MaterialIcons"/>
                      </Button>
                      <Button style={{ backgroundColor: '#DD5144' }}>
                        <Icon ios='ios-appstore' android="md-appstore" />
                      </Button>
                    </Fab>

                </Container>                );
    }
  }
export default Plugins;
