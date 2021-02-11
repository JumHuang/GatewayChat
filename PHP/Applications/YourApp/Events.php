<?php
/**
 * This file is part of workerman.
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the MIT-LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @author walkor<walkor@workerman.net>
 * @copyright walkor<walkor@workerman.net>
 * @link http://www.workerman.net/
 * @license http://www.opensource.org/licenses/mit-license.php MIT License
 */

/**
 * 用于检测业务代码死循环或者长时间阻塞等问题
 * 如果发现业务卡死，可以将下面declare打开（去掉//注释），并执行php start.php reload
 * 然后观察一段时间workerman.log看是否有process_timeout异常
 */
//declare(ticks=1);

use \GatewayWorker\Lib\Gateway;

/**
 * 主逻辑
 * 主要是处理 onConnect onMessage onClose 三个方法
 * onConnect 和 onClose 如果不需要可以不用实现并删除
 */
class Events
{

    /**
     * 当服务器启动的时候触发
     * @param worker businessWorker 进程实例
     */
    public static function onWorkerStart($businessWorker)
    {

    }

    /**
     * 当客户端连接时触发
     * 如果业务不需此回调可以删除onConnect
     * 
     * @param int $client_id 连接id
     */
    public static function onConnect($client_id)
    {
        // 向当前client_id发送数据 
        Gateway::sendToClient($client_id,  "{\"ClientId\":\"$client_id\",\"Message\":\"你好 $client_id ～\"}");
        // 向所有人发送
        Gateway::sendToAll( "{\"ClientId\":\"\",\"Message\":\"$client_id 上线了～\"}");
    }
    
    /**
     * 当客户端连接上gateway完成websocket握手时触发
     * @param int $client_id 连接id
     * @param mixed $data websocket握手时的http头数据
     */
    public static function onWebSocketConnect($client_id, $data)
    {
        var_export($data);
        if (!isset($data['get']['token'])) {
             Gateway::closeClient($client_id);
        }
    }
    
   /**
    * 当客户端发来消息时触发
    * @param int $client_id 连接id
    * @param mixed $message 具体消息
    */
   public static function onMessage($client_id, $message)
   {
        $json = json_decode($message, JSON_UNESCAPED_UNICODE);
        
        switch($json["Type"])
        {
        case "ping": //心跳检测
        //Gateway::sendToClient($client_id, "{\"ClientId\":\"$client_id\",\"Message\":\"已ping成功！\"}");
        break;
        case "sendToClient": //发送给单个Client
        // 转发消息给对应的客户端
        Gateway::sendToClient($json["ClientId"], "{\"ClientId\":\"$client_id\",\"Message\":\"".$json["Message"]."\"}");
        break;
        case "sendToAll": //发送给全部Client
        // 向所有人发送 
        Gateway::sendToAll("{\"ClientId\":\"$client_id\",\"Message\":\"".$json["Message"]."\"}");
        break;
        case "closeClient": //关闭单个Client
        break;
        case "isOnline": //单个Client是否在线
        break;
        case "bindUid": //绑定Uid
        break;
        case "unbindUid": //解绑Uid
        break;
        case "isUidOnline": //单个Uid是否在线
        break;
        case "getClientIdByUid": //通过Uid获取ClientId
        break;
        case "getUidByClientId": //通过ClientId获取Uid
        break;
        case "sendToUid": //发送给单个Uid
        Gateway::sendToUid($json["Uid"], $json["Message"]);
        break;
        case "joinGroup": //加入分组
        Gateway::joinGroup($client_id, $json["GroupId"]);
        Gateway::sendToGroup($json["GroupId"], "{\"ClientId\":\"$client_id\",\"Message\":\"$client_id 加入本群～快欢迎Ta～\"}");
        break;
        case "leaveGroup": //离开分组
        break;
        case "ungroup": //删除分组
        break;
        case "sendToGroup": //发送信息到分组
        Gateway::sendToGroup($json["GroupId"], "{\"ClientId\":\"$client_id\",\"Message\":\"".$json["Message"]."\"}");
        break;
        case "getClientIdCountByGroup": //通过分组获取ClientId在线数量
        break;
        case "getClientSessionByGroup": //通过分组获取在线的ClientId所有信息
        break;
        case "getAllClientIdCount": //获取ClientId在线总数
        break;
        case "getAllClientSessions": //获取所有在线ClientId信息
        break;
        case "setSession": //设置某个ClientId对应的Session
        break;
        case "updateSession": //更新某个ClientId对应的Session
        break;
        case "getSession": //获取某个ClientId对应的Session
        break;
        case "getClientIdListByGroup": //获取某个分组所有在线ClientId列表
        break;
        case "getAllClientIdList": //获取全局所有在线ClientId列表
        break;
        case "getUidListByGroup": //获取某个分组所有在线Uid列表
        break;
        case "getUidCountByGroup": //获取某个分组下的在线Uid数量
        break;
        case "getAllUidList": //获取全局所有在线Uid列表
        break;
        case "getAllUidCount": //获取全局所有在线Uid数量
        break;
        case "getAllGroupIdList": //获取全局所有在线Group Id列表
        break;
        default: //未知（未编写）类型接收
        break;
        }
   }
   
   /**
    * 当用户断开连接时触发
    * @param int $client_id 连接id
    */
   public static function onClose($client_id)
   {
       // 向所有人发送 
       GateWay::sendToAll("$client_id 退出了\r\n");
   }
   
   /**
     * 当服务器关闭的时候触发
     * @param worker businessWorker 进程实例
     */
    public static function onWorkerStop($businessWorker)
    {
       
    }
}
?>