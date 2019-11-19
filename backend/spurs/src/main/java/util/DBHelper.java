package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import servlet.SpursServlet;

public class DBHelper {
	
	public static void saveOrUpdate(Object object) {

		Session session = SpursServlet.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(object);
		session.getTransaction().commit();
		session.close();
	}
	
	public static Object getObject(Class c, Serializable id) {
		Session session = SpursServlet.sessionFactory.getCurrentSession();
		session.beginTransaction();
		Object o = session.get(c, id);
		session.close();
		return o;
	}
}
