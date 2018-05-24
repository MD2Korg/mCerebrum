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
      package_name: 'org.md2k.mcerebrum',
      tag: ['phone-accelerometer','phone-gyroscope','phone-compass','activity','gps','location'],
      current_version:'3.0.0',
      latest_version: '3.0.1',
      button_add: true,
      content: BACON_IPSUM
    },
    {
      title: 'Motion Sense',
      summary: 'Captures motion sense wrist sensor data',
      package_name: 'org.md2k.motionsense',
      download_link: 'https://raw.githubusercontent.com/MD2Korg/mCerebrum-releases/master/2.0/org.md2k.motionsense/motionsense2.0.17-RC1.apk',
      button_add: false,
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
    },
    card_button_text: {
      padding: 0,
      fontSize: 10,
    },
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
          style={{backgroundColor: '#131325'}}
          transition="backgroundColor"
        >
        <Card style={{backgroundColor: '#131325'}}>
                    <CardItem bordered style={{backgroundColor: '#1e2c3c'}}>
                      <Left>
                        <Body>
                          <Text style={{color: 'white'}}>{section.title}</Text>
                          <Text note>{section.summary}</Text>
                        </Body>
                      </Left>
                    </CardItem>
                    <CardItem boardered style={{backgroundColor: '#1e2c3c'}}>
                                  <Left>
                                    <Button transparent small warning disabled={this.addButtonStatus(section.package_name)}  onPress={() => NativeModules.ActivityStarter.pluginInstall(section.package_name)}>
                                    <Icon active name="ios-add-circle-outline" type="Ionicons"/>
                                      <Text style={styles.card_button_text}>Add</Text>
                                    </Button>
                                  </Left>
                                  <Body>
                                    <Button transparent small warning disabled={this.addButtonStatus(section.package_name)} onPress={() => NativeModules.ActivityStarter.pluginUnInstall(section.package_name)}>
                                      <Icon active name="ios-remove-circle-outline" type="Ionicons"/>
                                      <Text style={styles.card_button_text}>Remove</Text>
                                    </Button>
                                  </Body>
                                  <Right>
                                  <Button transparent small warning onPress={() => NativeModules.ActivityStarter.pluginSettings(section.package_name)}>
                                    <Icon active name="ios-settings-outline" type="Ionicons"/>
                                    <Text style={styles.card_button_text}>Settings</Text>
                                  </Button>
                                  </Right>
                                </CardItem>
                                                  </Card>
                          </Animatable.View>

      );
    }
    addButtonStatus=(packageName)=>{
      if (packageName=='org.md2k.mcerebrum')
        return true;
        else return false;
    }
    removeButtonStatus=(packageName)=>{
      if (packageName=='org.md2k.mcerebrum')
        return true;
        else return false;
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
      this.props.navigator.setTitle({title: 'mCerebrum : Plugins'});
    }

    render() {
      return (
        <Container style={{backgroundColor: '#131325'}}>
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
                </Container>                );
    }
  }

export default Plugins;
