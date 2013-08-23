package com.lsq.openfire.chat.logs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.util.ParamUtils;

import com.alibaba.fastjson.JSON;
import com.lsq.openfire.chat.logs.entity.ChatLogs;
public class ChatLogsServlet extends HttpServlet {
    private static final long serialVersionUID = 6981863134047161005L;
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static DbChatLogsManager logsManager;
    @Override

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logsManager = DbChatLogsManager.getInstance();
        // cancel auth check ,view without login
        AuthCheckFilter.addExclude("lsq");
        AuthCheckFilter.addExclude("lsq/chatlogs");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        ChatLogs entity = new ChatLogs();
        String action = ParamUtils.getParameter(request, "action");
        if ("last!contact".equals(action)) {
            String sender = ParamUtils.getParameter(request, "sender");
            entity.setSender(sender);
            List<String> result = logsManager.findLastContact(entity);
            request.setAttribute("lastContact", result);
//            response.sendRedirect("chatLogs-last-contact-service.jsp");
            request.getRequestDispatcher("chatLogs-last-contact-service.jsp").forward(request, response);
        } else if ("all!contact".equals(action)) {
            List<String> result = logsManager.findAllContact();
            request.setAttribute("allContact", result);
            request.getRequestDispatcher("chatLogs-all-contact-service.jsp").forward(request, response);
        } else if ("remove!contact".equals(action)) {
            Integer messageId = ParamUtils.getIntParameter(request, "messageId", -1);
            logsManager.remove(messageId);
            request.getRequestDispatcher("chatLogs-service.jsp").forward(request, response);
        } else if ("lately!contact".equals(action)) {
            String sender = ParamUtils.getParameter(request, "sender");
            entity.setSender(sender);
            List<String> result = logsManager.findLastContact(entity);
            replyMessage(JSON.toJSONString(result), response, out);
        } else if ("entire!contact".equals(action)) {
            List<String> result = logsManager.findAllContact();
            replyMessage(JSON.toJSONString(result), response, out);
        } else if ("delete!contact".equals(action)) {
            Integer messageId = ParamUtils.getIntParameter(request, "messageId", -1);
            replyMessage(JSON.toJSONString(logsManager.remove(messageId)), response, out);
        } else if ("query".equals(action)) {
            String sender = ParamUtils.getParameter(request, "sender");
            String receiver = ParamUtils.getParameter(request, "receiver");
            String content = ParamUtils.getParameter(request, "content");
            String createDate = ParamUtils.getParameter(request, "createDate");
            try {
                if (createDate != null && !"".equals(createDate)) {
                    entity.setCreateDate(new Timestamp(df.parse(createDate).getTime()));
                }
            } catch (Exception e) {
            }

            entity.setContent(content);
            entity.setReceiver(receiver);
            entity.setSender(sender);

            List<ChatLogs> logs = logsManager.query(entity);
            replyMessage(JSON.toJSONString(logs), response, out);

        } else {
            String sender = ParamUtils.getParameter(request, "sender");
            String receiver = ParamUtils.getParameter(request, "receiver");
            String content = ParamUtils.getParameter(request, "content");
            String createDate = ParamUtils.getParameter(request, "createDate");

            try {
                if (createDate != null && !"".equals(createDate)) {
                    entity.setCreateDate(new Timestamp(df.parse(createDate).getTime()));
                }
            } catch (Exception e) {
            }

            entity.setContent(content);
            entity.setReceiver(receiver);
            entity.setSender(sender);

            List<HashMap<String, Object>> logs = logsManager.search(entity);
            replyMessage(JSON.toJSONString(logs), response, out);
        }
    }
 

    @Override
    public void destroy() {
        super.destroy();
        // Release the excluded URL
        AuthCheckFilter.removeExclude("lsq/chatlogs");

        AuthCheckFilter.removeExclude("lsq");
    }

    private void replyMessage(String message, HttpServletResponse response, PrintWriter out) {
        response.setContentType("text/json");
        out.println(message);
        out.flush();
    }
}
