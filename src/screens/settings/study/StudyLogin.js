import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  ImageBackground,
  Dimensions,
  TextInput,
  Button,
  KeyboardAvoidingView,
  TouchableOpacity
} from 'react-native';
import { Icon } from 'native-base';

const { width, height } = Dimensions.get("window");

const mark = require("../../../../img/cc.png");

export default class LoginScreen extends Component {
  render() {
    return (
      <View style={styles.container}>
        <KeyboardAvoidingView style={styles.container} behavior="padding" enabled>
          <View style={styles.markWrap}>
            <ImageBackground source={mark} style={styles.mark} resizeMode="contain" />
          </View>
          <View style={styles.wrapper}>
            <View style={styles.inputWrap}>
              <View style={styles.iconWrap}>
              <Icon type="Ionicons" name="md-person" style={{fontSize: 20, color: 'white'}}/>
              </View>
              <TextInput
                placeholder="Username"
                placeholderTextColor="#FFF"
                underlineColorAndroid="transparent"
                style={styles.input}
              />
            </View>
            <View style={styles.inputWrap}>
              <View style={styles.iconWrap}>
              <Icon type="Ionicons" name="md-lock" style={{fontSize: 20, color: 'white'}}/>
              </View>
              <TextInput
                placeholderTextColor="#FFF"
                placeholder="Password"
                underlineColorAndroid="transparent"
                style={styles.input}
                secureTextEntry
              />
            </View>
            <View style={styles.inputWrap}>
              <View style={styles.iconWrap}>
              <Icon type="MaterialCommunityIcons" name="web" style={{fontSize: 20, color: 'white'}}/>
              </View>
              <TextInput
                placeholder="Server"
                placeholderTextColor="#FFF"
                underlineColorAndroid="transparent"
                style={styles.input}
              />
            </View>
            <TouchableOpacity activeOpacity={.5}>
              <View>
              </View>
            </TouchableOpacity>
            <TouchableOpacity activeOpacity={.5}>
              <View style={styles.button}>
                <Text style={styles.buttonText}>Sign In</Text>
              </View>
            </TouchableOpacity>
          </View>
          <View style={styles.container}>
            <View style={styles.signupWrap}>
              <TouchableOpacity activeOpacity={.5}>
                <View>
                </View>
              </TouchableOpacity>
            </View>
          </View>
          </KeyboardAvoidingView>
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#131325',
  },
  markWrap: {
    flex: 1,
    paddingVertical: 30,
  },
  mark: {
    width: null,
    height: null,
    flex: 1,
  },
  background: {
    width,
    height,
  },
  wrapper: {
    paddingVertical: 30,
  },
  inputWrap: {
    flexDirection: "row",
    marginVertical: 10,
    height: 40,
    borderBottomWidth: 1,
    borderBottomColor: "#CCC"
  },
  iconWrap: {
    paddingHorizontal: 7,
    alignItems: "center",
    justifyContent: "center",
  },
  icon: {
    height: 20,
    width: 20,
  },
  input: {
    flex: 1,
    paddingHorizontal: 10,
    color: "#FFF",
  },
  button: {
    backgroundColor: "#FF3366",
    paddingVertical: 20,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 30,
  },
  buttonText: {
    color: "#FFF",
    fontSize: 18,
  },
  forgotPasswordText: {
    color: "#D8D8D8",
    backgroundColor: "transparent",
    textAlign: "right",
    paddingRight: 15,
  },
  signupWrap: {
    backgroundColor: "transparent",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
  },
  accountText: {
    color: "#D8D8D8"
  },
  signupLinkText: {
    color: "#FFF",
    marginLeft: 5,
  }
});
