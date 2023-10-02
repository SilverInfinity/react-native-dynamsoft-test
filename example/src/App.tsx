// @ts-nocheck
import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import DynasoftBridge, { DynasoftView } from 'react-native-dynasoft';

const licenseKey = 'DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9';
const badLicenseKey = 'DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ8';

export default function App() {
  const [result, setResult] = React.useState();
  React.useEffect(() => {
    DynasoftBridge.multiply(3, 7).then(setResult);
  }, []);

  const [licensed, setLicenced] = React.useState(false);
  console.log('render', result);
  React.useEffect(() => {
    DynasoftBridge.setLicenceKey(licenseKey)
      .then((res) => {
        console.log('it returned!', res);
        setLicenced(res);
      })
      .catch((e) => {
        console.log(e);
      });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Text>Licensed: {licensed ? 'yes' : 'no'}</Text>
      <DynasoftView color="#32a852" style={styles.box} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 400,
    height: 600,
    marginVertical: 20,
  },
});
