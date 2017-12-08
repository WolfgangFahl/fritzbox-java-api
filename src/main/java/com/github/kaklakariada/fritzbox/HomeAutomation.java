/**
 * A Java API for managing FritzBox HomeAutomation
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.kaklakariada.fritzbox;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaklakariada.fritzbox.http.QueryParameters;
import com.github.kaklakariada.fritzbox.http.QueryParameters.Builder;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

/**
 * home automation functions
 */
public class HomeAutomation {

  private final static Logger LOG = LoggerFactory
      .getLogger(HomeAutomation.class);
  private final static String HOME_AUTOMATION_PATH = "/webservices/homeautoswitch.lua";

  private final FritzBoxSessionImpl session;

  public FritzBoxSessionImpl getSession() {
    return session;
  }

  /**
   * construct me from the given fritz box session
   * 
   * @param fritzbox
   */
  private HomeAutomation(FritzBoxSessionImpl fritzbox) {
    this.session = fritzbox;
  }

  /**
   * connect to the home automation
   * 
   * @param baseUrl
   * @param username
   * @param password
   * @return - a home automation object
   */
  public static HomeAutomation connect(String baseUrl, String username,
      String password) {
    final FritzBoxSessionImpl session = new FritzBoxSessionImpl(baseUrl);
    session.login(username, password);
    return new HomeAutomation(session);
  }

  /**
   * connect to the home automation with the given configuration
   * 
   * @param config
   * @return - the home automation
   */
  public static HomeAutomation connect(Config config) {
    final HomeAutomation homeAutomation = connect(config.baseUrl,
        config.username, config.password);
    return homeAutomation;
  }

  public DeviceList getDeviceListInfos() {
    final DeviceList deviceList = executeCommand("getdevicelistinfos",
        DeviceList.class);
    LOG.trace("Found {} devices, devicelist version: {}",
        deviceList.getDevices().size(), deviceList.getApiVersion());
    return deviceList;
  }

  private <T> T executeCommand(String command, Class<T> resultType) {
    final QueryParameters parameters = QueryParameters.builder()
        .add("switchcmd", command).build();
    return session.getAutenticated(HOME_AUTOMATION_PATH, parameters,
        resultType);
  }

  private <T> T executeDeviceCommand(String deviceAin, String command,
      String parameter, Class<T> responseType) {
    final Builder paramBuilder = QueryParameters.builder().add("ain", deviceAin)
        .add("switchcmd", command);
    if (parameter != null) {
      paramBuilder.add("param", parameter);
    }
    return session.getAutenticated(HOME_AUTOMATION_PATH, paramBuilder.build(),
        responseType);
  }

  public List<String> getSwitchList() {
    final String switches = executeCommand("getswitchlist", String.class);
    final List<String> idList = Arrays.asList(switches.split(","));
    LOG.trace("Got switch list string '{}': {}", switches, idList);
    return idList;
  }

  public void switchPowerState(String deviceAin, boolean on) {
    final String command = on ? "setswitchon" : "setswitchoff";
    executeDeviceCommand(deviceAin, command, null, Boolean.class);
  }

  public void togglePowerState(String deviceAin) {
    executeDeviceCommand(deviceAin, "setswitchtoggle", null, Boolean.class);
  }

  public boolean getSwitchState(String deviceAin) {
    return executeDeviceCommand(deviceAin, "getswitchstate", null,
        Boolean.class);
  }

  public boolean getSwitchPresent(String deviceAin) {
    return executeDeviceCommand(deviceAin, "getswitchpresent", null,
        Boolean.class);
  }

  public String getSwitchName(String deviceAin) {
    return executeDeviceCommand(deviceAin, "getswitchname", null, String.class);
  }

  public Float getTemperature(String deviceAin) {
    final Integer centiDegree = executeDeviceCommand(deviceAin,
        "gettemperature", null, Integer.class);
    return centiDegree == null ? null : centiDegree / 10F;
  }

  public Float getSwitchPowerWatt(String deviceAin) {
    final Integer powerMilliWatt = executeDeviceCommand(deviceAin,
        "getswitchpower", null, Integer.class);
    return powerMilliWatt == null ? null : powerMilliWatt / 1000F;
  }

  public Integer getSwitchEnergyWattHour(String deviceAin) {
    return executeDeviceCommand(deviceAin, "getswitchenergy", null,
        Integer.class);
  }

  public void logout() {
    session.logout();
  }

}
