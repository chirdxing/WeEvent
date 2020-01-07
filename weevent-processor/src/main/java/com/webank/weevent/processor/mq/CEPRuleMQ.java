package com.webank.weevent.processor.mq;

<<<<<<< HEAD
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
=======
import java.io.IOException;
import java.nio.charset.StandardCharsets;
>>>>>>> f0437ed
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webank.weevent.processor.model.CEPRule;
<<<<<<< HEAD
import com.webank.weevent.processor.service.AnalysisWeEventIdService;
import com.webank.weevent.processor.utils.CommonUtil;
=======
import com.webank.weevent.processor.model.StatisticRule;
import com.webank.weevent.processor.model.StatisticWeEvent;
import com.webank.weevent.processor.utils.CommonUtil;
import com.webank.weevent.processor.utils.ConstantsHelper;

import com.webank.weevent.processor.utils.DataBaseUtil;
import com.webank.weevent.processor.utils.JsonUtil;
import com.webank.weevent.processor.utils.RetCode;
import com.webank.weevent.processor.utils.StatisticCEPRuleUtil;
>>>>>>> f0437ed
import com.webank.weevent.sdk.BrokerException;
import com.webank.weevent.sdk.IWeEventClient;
import com.webank.weevent.sdk.SendResult;
import com.webank.weevent.sdk.WeEvent;

<<<<<<< HEAD
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
=======
import javafx.util.Pair;
>>>>>>> f0437ed
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.springframework.util.StringUtils;

@Slf4j
public class CEPRuleMQ {
    // <ruleId <--> subscriptionId>
<<<<<<< HEAD
    public static Map<String, String> subscriptionIdMap = new ConcurrentHashMap<>();

    public static void updateSubscribeMsg(CEPRule rule, Map<String, CEPRule> ruleMap) throws BrokerException {
        // unsubscribe old the topic
        ruleMap.get(rule.getId()).getToDestination();
        IWeEventClient client = getClient(rule);
        // update the rule map
        ruleMap.put(rule.getId(), rule);
        // update subscribe
        subscribeMsg(rule, ruleMap);
        client.unSubscribe(subscriptionIdMap.get(rule.getId()));
=======
    private static Map<String, String> subscriptionIdMap = new ConcurrentHashMap<>();
    // <subscriptionId-->client session>
    private static Map<String, IWeEventClient> subscriptionClientMap = new ConcurrentHashMap<>();
    // Pair<key, value>--><WeEvent, CEPRule>
    private static BlockingDeque<Pair<WeEvent, CEPRule>> systemMessageQueue = new LinkedBlockingDeque<>();
    // client --><brokerurl,groupId>
    private static Map<IWeEventClient, Pair<String, String>> clientGroupMap = new ConcurrentHashMap<>();

    private static CEPRuleMQ.DBThread dbThread = new CEPRuleMQ.DBThread();

    // statistic weevent
    private static StatisticWeEvent statisticWeEvent = new StatisticWeEvent();

    @PostConstruct
    public void init() {
        // get all rule
        log.info("start dBThread ...");
        new Thread(dbThread).start();
    }

    public static void updateSubscribeMsg(CEPRule rule, Map<String, CEPRule> ruleMap) throws BrokerException {
        // when is in run status. update the rule map
        // update unsubscribe
        String subId = subscriptionIdMap.get(rule.getId());
        statisticWeEvent = StatisticCEPRuleUtil.statistic(statisticWeEvent, ruleMap);
        if (1 == rule.getStatus()) {
            if (null != subId) {
                IWeEventClient client = subscriptionClientMap.get(subId);
                // if they are equal
                for (Map.Entry<String, CEPRule> entry : ruleMap.entrySet()) {
                    if (!(rule.getFromDestination().equals(entry.getValue().getFromDestination()))) {
                        boolean flag = client.unSubscribe(subId);
                        log.info("start rule ,and subscribe flag:{}", flag);
                    }
                }

                subscribeMsg(rule, ruleMap, client);

            } else {
                ruleMap.put(rule.getId(), rule);
                // update subscribe
                subscribeMsg(rule, ruleMap, null);
                log.info("start rule ,and subscribe rule:{}", rule.getId());
            }
        }
        if (0 == rule.getStatus() || 2 == rule.getStatus()) {
            log.info("stop,update,delete rule subscriptionIdMap.size:{}", subscriptionIdMap.size());

            log.info("stop,update,delete rule ,and unsubscribe,subId :{}", subId);
            if (null != subId) {
                IWeEventClient client = subscriptionClientMap.get(subId);
                boolean flag = client.unSubscribe(subId);
                if (!StringUtils.isEmpty(subscriptionClientMap.get(subId))) {
                    clientGroupMap.remove(subscriptionClientMap.get(subId));
                    subscriptionIdMap.remove(rule.getId());
                    subscriptionClientMap.remove(subId);
                }

                log.info("stop,update,delete rule ,and unsubscribe return {}", flag);
            }

        }
>>>>>>> f0437ed
    }

    private static IWeEventClient getClient(CEPRule rule) {
        try {
            Map<String, String> mapRequest = CommonUtil.uRLRequest(rule.getBrokerUrl());
            String baseUrl = CommonUtil.urlPage(rule.getBrokerUrl());
            IWeEventClient client;
            if (null != mapRequest.get("groupId")) {
                //  client = IWeEventClient.build(baseUrl,mapRequest.get("groupId"));
                client = IWeEventClient.build(baseUrl);
            } else {
                client = IWeEventClient.build(baseUrl);
            }
            return client;
        } catch (BrokerException e) {
            log.info("BrokerException{}", e.toString());
            return null;
        }

    }

    public static void subscribeMsg(CEPRule rule, Map<String, CEPRule> ruleMap) {
        try {
            IWeEventClient client = getClient(rule);
            // subscribe topic
            log.info("subscribe topic:{}", rule.getFromDestination());
<<<<<<< HEAD
            String subscriptionId = client.subscribe(rule.getFromDestination(), WeEvent.OFFSET_LAST, new IWeEventClient.EventListener() {
                @Override
                public void onEvent(WeEvent event) {
                    try {
                        String content = new String(event.getContent());
                        log.info("on event:{},content:{}", event.toString(), content);

                        if (CommonUtil.checkValidJson(content)) {
                            handleOnEvent(event, client, ruleMap);
=======
            String subscriptionId;
            if (StringUtils.isEmpty(rule.getOffSet())) {
                subscriptionId = client.subscribe(rule.getFromDestination(), WeEvent.OFFSET_LAST, new IWeEventClient.EventListener() {
                    @Override
                    public void onEvent(WeEvent event) {
                        try {
                            String content = new String(event.getContent());
                            log.info("on event:{},content:{}", event.toString(), content);

                            Pair<String, String> type;
                            // check the content
                            if (JsonUtil.isValid(content)) {
                                type = handleOnEvent(client, event, ruleMap);
                            } else {
                                type = handleOnEventOtherPattern(client, event, ruleMap);
                            }
                            statisticWeEvent = StatisticCEPRuleUtil.statisticOrderType(statisticWeEvent, type);
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.info("on event:{}", e.toString());
                    }
                });
            } else {
                subscriptionId = client.subscribe(rule.getFromDestination(), rule.getOffSet(), new IWeEventClient.EventListener() {
                    @Override
                    public void onEvent(WeEvent event) {
                        try {

                            String content = new String(event.getContent());
                            log.info("on event:{},content:{}", event.toString(), content);
                            Pair<String, String> type;
                            // check the content
                            if (JsonUtil.isValid(content)) {
                                type = handleOnEvent(client, event, ruleMap);
                            } else {
                                type = handleOnEventOtherPattern(client, event, ruleMap);
                            }
                            statisticWeEvent = StatisticCEPRuleUtil.statisticOrderType(statisticWeEvent, type);
                        } catch (Exception e) {
                            log.error(e.toString());
>>>>>>> f0437ed
                        }
                        //Analysis WeEventId  to the governance database
                        AnalysisWeEventIdService.analysisWeEventId(rule, event.getEventId());
                    } catch (JSONException | BrokerException e) {
                        log.error(e.toString());
                    }
                }

                @Override
                public void onException(Throwable e) {
                    log.info("on event:{}", e.toString());
                }
            });
            subscriptionIdMap.put(rule.getId(), subscriptionId);
        } catch (BrokerException e) {
            log.info("BrokerException{}", e.toString());
        }
    }

    public static void unSubscribeMsg(CEPRule rule, String subscriptionId) {
        try {
            IWeEventClient client = getClient(rule);
            log.info("id:{},sunid:{}", rule.getId(), subscriptionId);
            client.unSubscribe(subscriptionId);
        } catch (BrokerException e) {
            log.info("BrokerException{}", e.toString());
        }
    }

<<<<<<< HEAD
    private static void sendMessageToDB(String content, CEPRule rule) {
        JSONObject eventContent = JSONObject.parseObject(content);
        try {
            Connection conn = CommonUtil.getConnection(rule.getDatabaseUrl());

            if (conn != null) {
                Map<String, String> urlParamMap = CommonUtil.uRLRequest(rule.getDatabaseUrl());
                StringBuffer insertExpression = new StringBuffer("insert into ");
                insertExpression.append(urlParamMap.get("tableName"));
                insertExpression.append("(");
                StringBuffer values = new StringBuffer("values (");
                Map<String, Integer> sqlvalue = CommonUtil.contactsql(content, rule.getPayload());
                List<String> result = new ArrayList(sqlvalue.keySet());

                //contact insert into users (first_name, last_name, date_created, is_admin, num_points)
                for (Map.Entry<String, Integer> entry : sqlvalue.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                    if (entry.getValue().equals(1)) {
                        if (entry.getValue().equals(result.get(result.size() - 1))) {
                            insertExpression.append(entry.getKey()).append(")");
                            values.append("?）");
                        } else {
                            insertExpression.append(entry.getKey()).append(",");
                            values.append("?，");
                        }
                    }
                }

                StringBuffer query = insertExpression.append(values);
                log.info("query:{}", query);
                PreparedStatement preparedStmt = conn.prepareStatement(query.toString());
                for (int t = 0; t < result.size(); t++) {
                    preparedStmt.setString(t + 1, eventContent.get(result.get(t)).toString());
                }
                // execute the preparedstatement
                preparedStmt.execute();
                conn.close();
            }
        } catch (SQLException e) {
            log.info(e.toString());
        }

    }

    public static void handleOnEvent(WeEvent event, IWeEventClient client, Map<String, CEPRule> ruleMap) {
        log.info("handleOnEvent ruleMapsize :{}", ruleMap.size());

        // get the content ,and parsing it  byte[]-->String
        String content = new String(event.getContent());

        // match the rule and send message
        for (Map.Entry<String, CEPRule> entry : ruleMap.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue().getPayload())
                    && !StringUtils.isEmpty(entry.getValue().getConditionField())) {

                // parsing the payload && match the content,if true and hit it
                if (entry.getValue().getConditionType().equals(2)) {
                    sendMessageToDB(content, entry.getValue());

                } else if (hitRuleEngine(entry.getValue().getPayload(), content, entry.getValue().getConditionField())) {

                    // select the field and publish the message to the toDestination
                    try {
                        if (entry.getValue().getConditionType().equals(1)) {
                            // publish the message
                            log.info("publish topic {}", entry.getValue().getSelectField());
                            client.publish(entry.getValue().getToDestination(), content.getBytes());
=======
    private static Pair<String, String> handleOnEventOtherPattern(IWeEventClient client, WeEvent event, Map<String, CEPRule> ruleMap) {
        log.info("handleOnEvent ruleMapsize :{}", ruleMap.size());

        // match the rule and send message
        for (Map.Entry<String, CEPRule> entry : ruleMap.entrySet()) {
            StatisticRule rule = statisticWeEvent.getStatisticRuleMap().get(entry.getValue().getId());

            log.info("group:{},client:brokerUrl:{},rule:brokerUr{}", clientGroupMap.get(client).getValue().equals(entry.getValue().getGroupId()), clientGroupMap.get(client).getKey(), CommonUtil.urlPage(entry.getValue().getBrokerUrl()));
            // check the broker and groupid
            if (!(clientGroupMap.get(client).getValue().equals(entry.getValue().getGroupId()) && clientGroupMap.get(client).getKey().equals(CommonUtil.urlPage(entry.getValue().getBrokerUrl())))) {
                // update the  statistic weevent
                rule.setNotHitTimes(rule.getNotHitTimes() + 1);
                continue;
            }
            // write the # topic to history db
            if ("1".equals(entry.getValue().getSystemTag()) && entry.getValue().getFromDestination().equals("#") && entry.getValue().getConditionType().equals(2)) {

                log.info("system insert db:{}", entry.getValue().getId());
                Pair<WeEvent, CEPRule> messagePair = new Pair<>(event, entry.getValue());
                systemMessageQueue.add(messagePair);
                // update the  statistic weevent
                return new Pair<>(ConstantsHelper.HIT_TIMES, entry.getValue().getId());
            } else {
                return new Pair<>(ConstantsHelper.NOT_HIT_TIMES, entry.getValue().getId());
            }
        }
        return new Pair<>(ConstantsHelper.OTHER, "");
    }

    private static boolean checkTheInput(Map.Entry<String, CEPRule> entry, IWeEventClient client) {
        if (StringUtils.isEmpty(subscriptionIdMap.get(entry.getValue().getId())) || StringUtils.isEmpty(subscriptionClientMap.get(subscriptionIdMap.get(entry.getValue().getId())))) {
            return true;
        }

        log.debug("client:{}group:{},client:brokerUrl:{},rule:brokerUr{}", subscriptionClientMap.get(subscriptionIdMap.get(entry.getValue().getId())).equals(client), clientGroupMap.get(client).getValue().equals(entry.getValue().getGroupId()), clientGroupMap.get(client).getKey(), CommonUtil.urlPage(entry.getValue().getBrokerUrl()));
        return (!(subscriptionClientMap.get(subscriptionIdMap.get(entry.getValue().getId())).equals(client) && clientGroupMap.get(client).getValue().equals(entry.getValue().getGroupId()) && clientGroupMap.get(client).getKey().equals(CommonUtil.urlPage(entry.getValue().getBrokerUrl()))));

    }

    private static Pair<String, String> handleOnEvent(IWeEventClient client, WeEvent event, Map<String, CEPRule> ruleMap) throws IOException {
        log.info("handleOnEvent ruleMapsize :{}", ruleMap.size());
        // match the rule and send message
        for (Map.Entry<String, CEPRule> entry : ruleMap.entrySet()) {
            StatisticRule rule = statisticWeEvent.getStatisticRuleMap().get(entry.getValue().getId());

            // check the parameter
            if (checkTheInput(entry, client)) {
                continue;
            }

            // write the # topic to history db  or ifttt message
            if ("1".equals(entry.getValue().getSystemTag()) && entry.getValue().getFromDestination().equals("#") && entry.getValue().getConditionType().equals(2)) {
                log.info("system insert db:{}", entry.getValue().getId());
                Pair<WeEvent, CEPRule> messagePair = new Pair<>(event, entry.getValue());
                systemMessageQueue.add(messagePair);

                return new Pair<>(ConstantsHelper.HIT_TIMES, entry.getValue().getId());

            } else {

                if (StringUtils.isEmpty(entry.getValue().getSelectField()) || (StringUtils.isEmpty(entry.getValue().getPayload()))) {
                    continue;
                }
                // hit the rule engine
                if (hitRuleEngine(entry.getValue(), event)) {
                    try {
                        // update the  statistic weevent
                        rule.setHitTimes(rule.getHitTimes() + 1);
                        // get the system parameter
                        String groupId = entry.getValue().getGroupId();

                        // parsing the payload && match the content,if true and hit it
                        if (entry.getValue().getConditionType().equals(2)) {

                            log.info("entry: {},event hit the db and insert: {}", entry.getValue().toString(), event.toString());

                            // send to database
                            String ret = DataBaseUtil.sendMessageToDB(event, entry.getValue());
                            return new Pair<>(ret, entry.getValue().getId());
                        } else if (entry.getValue().getConditionType().equals(1)) {

                            // select the field and publish the message to the toDestination
                            String eventContent = CommonUtil.setWeEventContent(entry.getValue().getBrokerId(), groupId, event, entry.getValue().getSelectField(), entry.getValue().getPayload());

                            // publish the message
                            WeEvent weEvent = new WeEvent(entry.getValue().getToDestination(), eventContent.getBytes(StandardCharsets.UTF_8), event.getExtensions());
                            log.info("after hitRuleEngine weEvent  groupId: {}, event:{}", groupId, weEvent.toString());
                            IWeEventClient toDestinationClient = getClient(entry.getValue());
                            SendResult result = toDestinationClient.publish(weEvent);

                            // update the  statistic weevent
                            if ("SUCCESS".equals(result.getStatus())) {
                                return new Pair<>(ConstantsHelper.PUBLISH_EVENT_SUCCESS, entry.getValue().getId());
                            } else {
                                return new Pair<>(ConstantsHelper.PUBLISH_EVENT_FAIL, entry.getValue().getId());
                            }
>>>>>>> f0437ed
                        }
                    } catch (BrokerException e) {
                        log.error(e.toString());
                        return new Pair<>(ConstantsHelper.LAST_FAIL_REASON, entry.getValue().getId());
                    }
                } else {
                    return new Pair<>(ConstantsHelper.NOT_HIT_TIMES, entry.getValue().getId());
                }
            }
<<<<<<< HEAD

=======
        }
        return new Pair<>(ConstantsHelper.OTHER, "");
    }


    private static boolean handleTheEqual(WeEvent eventMessage, String condition) throws IOException {
        String eventContent = new String(eventMessage.getContent());
        Map event = JsonUtil.parseObject(eventContent, Map.class);
        String[] strs = condition.split("=");
        if (strs.length == 2) {
            // event contain left key
            if (event.containsKey(strs[0]) && event.get(strs[0]).toString().equals(strs[1])) {
                log.info("get the a=1 pattern {}", "true");
                return true;
            } else {
                return false;
            }
>>>>>>> f0437ed
        }

    }

<<<<<<< HEAD

    private static boolean hitRuleEngine(String payload, String eventContent, String condition) {
        if (CommonUtil.checkJson(eventContent, payload)) {
            List<String> eventContentKeys = CommonUtil.getKeys(payload);
            JSONObject event = JSONObject.parseObject(eventContent);
            JexlEngine jexl = new JexlBuilder().create();

            JexlContext context = new MapContext();
            for (String key : eventContentKeys) {
                context.set(key, event.get(key));
=======
    private static boolean hitRuleEngine(CEPRule rule, WeEvent eventMessage) throws IOException {
        // String payload, WeEvent eventMessage, String condition
        String payload = rule.getPayload();
        String condition = rule.getConditionField();
        String[][] systemFunctionMessage = CommonUtil.stringConvertArray(rule.getSystemFunctionMessage());
        try {
            String eventContent = new String(eventMessage.getContent());
            // all parameter must be the same
            if (CommonUtil.checkJson(eventContent, payload) && (StringUtils.isEmpty(condition))) {
                // if the condition is empty, just return all message
                return true;
            } else if (CommonUtil.checkJson(eventContent, payload)) {
                List<String> eventContentKeys = CommonUtil.getKeys(payload);
                Map event = JsonUtil.parseObject(eventContent, Map.class);
                JexlEngine jexl = new JexlBuilder().create();

                JexlContext context = new MapContext();
                for (String key : eventContentKeys) {
                    context.set(key, event.get(key));
                }

                // check the expression ,if match then true
                log.info("condition:{}", condition);
                if (!StringUtils.isEmpty(rule.getSystemFunctionMessage())) {
                    String[][] systemFunctionDetail = CommonUtil.stringConvertArray(rule.getSystemFunctionMessage());
                    if (0 != systemFunctionDetail.length) {
                        condition = CommonUtil.analysisSystemFunction(systemFunctionMessage, eventContent, condition);
                    }
                }

                boolean checkFlag = (Boolean) jexl.createExpression(condition).evaluate(context);
                log.info("payload:{},eventContent:{},condition:{},hit rule:{}", payload, eventContent, condition, checkFlag);
                return checkFlag;
            }
            log.info("payload:{},eventContent:{},condition:{},hit rule:false", payload, eventContent, condition);
            return false;
        } catch (Exception e) {
            if (handleTheEqual(eventMessage, condition)) {
                log.info("single equal match");
                return true;
            } else {
                log.info("error number");
                return false;
            }

        }
    }

    /**
     * check the condition field
     *
     * @param payload payload message
     * @param condition condition details
     * @return result code
     */
    public static RetCode checkCondition(String payload, String condition) {
        try {
            List<String> payloadContentKeys = CommonUtil.getKeys(payload);
            Map payloadJson = JsonUtil.parseObject(payload, Map.class);
            JexlEngine jexl = new JexlBuilder().create();

            JexlContext context = new MapContext();
            for (String key : payloadContentKeys) {
                context.set(key, payloadJson.get(key));
            }
            Map event = JsonUtil.parseObject(payload, Map.class);
            String[] strs = condition.split("=");
            boolean flag = false;
            if (strs.length == 2 && !(strs[0].contains("<") || strs[0].contains(">") || (strs[1].contains("<") || strs[1].contains(">")))) {
                flag = true;
            }
            if (flag) {
                // event contain left key
                if (event.containsKey(strs[0])) {
                    if (event.get(strs[0]) instanceof String) {
                        return ConstantsHelper.SUCCESS;

                    } else {
                        if (event.get(strs[0]) instanceof Number) {
                            if (strs[1].matches("[0-9]+")) {
                                return ConstantsHelper.SUCCESS;

                            } else {
                                return ConstantsHelper.FAIL;

                            }
                        }
                    }
                } else {
                    return ConstantsHelper.FAIL;
                }
            } else {
                Boolean e = (Boolean) jexl.createExpression(condition).evaluate(context);
                log.info("result:{}", e);
                return ConstantsHelper.SUCCESS;

            }
        } catch (Exception e) {
            log.info("error number");
            return ConstantsHelper.FAIL;
        }
        return ConstantsHelper.FAIL;
    }

    public static StatisticWeEvent getStatisticWeEvent() {
        log.info("getStatisticWeEvent:{}", statisticWeEvent);
        return statisticWeEvent;
    }

    private static class DBThread implements Runnable {

        public void run() {
            while (true) {
                try {
                    // if the quene is null,then the thread sleep 1s
                    long ideaTime = 1000L;
                    Pair<WeEvent, CEPRule> item = systemMessageQueue.poll(ideaTime, TimeUnit.MILLISECONDS);

                    if (null != item) {
                        log.info("auto redo thread enter,system insert db:{}", item.getValue().getId());
                        //  send to  the db
                        DataBaseUtil.sendMessageToDB(item.getKey(), item.getValue());
                    }
                } catch (InterruptedException e) {
                    log.info(e.toString());
                }
>>>>>>> f0437ed
            }
            // Create an expression  "a>10"
            return (Boolean) jexl.createExpression(condition).evaluate(context);
        }
        return Boolean.FALSE;
    }
}
