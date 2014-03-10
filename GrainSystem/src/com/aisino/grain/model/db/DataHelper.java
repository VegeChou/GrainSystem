package com.aisino.grain.model.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DataHelper extends OrmLiteSqliteOpenHelper {
	private static String TAG = "DataHelper";
	private static final String DATABASE_NAME = "test.db"; // ���ݿ���
	private static final int DATABASE_VERSION = 1; // ���ݿ�汾��,�Ժ������ʵ����������޸�,��Ҫ����һ�����ݿ�Ļ�,��һ�°汾�ž�����
	
	Dao <AcountInfo, Integer> AcountInfoDao = null;
	Dao <Warehouse, Integer> WarehouseDao = null;
	Dao <BusinessType, Integer> BusinessTypeDao = null;
	Dao <GrainTypeDB, Integer> GrainTypeDao = null;
	Dao <QualityIndex, Integer> QualityIndexDao = null;
	Dao <SystenConfig, Integer> SystenConfigDao = null;
	
	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, AcountInfo.class);// ����AcountInfo��
			TableUtils.createTable(connectionSource, Warehouse.class);// ����Warehouse��
			TableUtils.createTable(connectionSource, BusinessType.class);// ����BusinessType��
			TableUtils.createTable(connectionSource, GrainTypeDB.class);// ����GrainType��
			TableUtils.createTable(connectionSource, QualityIndex.class);// ����QualityIndex��
			TableUtils.createTable(connectionSource, SystenConfig.class);// ����SystenConfig��
			Log.v(TAG, "������ɹ�!");
		} catch (SQLException e) {
			e.printStackTrace();
		} 

	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// ���ݿ��⵽�汾�Ų�ͬʱ�Զ�ִ��,����������Ӧ�ã�
		try {
			// 1:ɾ��ԭ�еľɱ�
			TableUtils.dropTable(connectionSource, AcountInfo.class, true);
			TableUtils.dropTable(connectionSource, Warehouse.class, true);
			TableUtils.dropTable(connectionSource, BusinessType.class, true);
			TableUtils.dropTable(connectionSource, GrainTypeDB.class, true);
			TableUtils.dropTable(connectionSource, QualityIndex.class, true);
			TableUtils.dropTable(connectionSource, SystenConfig.class, true);
			// 2:�������ڵ��±�
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Dao<AcountInfo, Integer> getAcountInfoDao() {
		if (AcountInfoDao == null) {
			try {
				AcountInfoDao = getDao(AcountInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return AcountInfoDao;
	}
	
	public Dao<Warehouse, Integer> getWarehouseDao() {
		if (WarehouseDao == null) {
			try {
				WarehouseDao = getDao(Warehouse.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return WarehouseDao;
	}
	
	public Dao<BusinessType, Integer> getBusinessTypeDao() {
		if (BusinessTypeDao == null) {
			try {
				BusinessTypeDao = getDao(BusinessType.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return BusinessTypeDao;
	}
	
	public Dao<GrainTypeDB, Integer> getGrainTypeDao() {
		if (GrainTypeDao == null) {
			try {
				GrainTypeDao = getDao(GrainTypeDB.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return GrainTypeDao;
	}
	
	public Dao<QualityIndex, Integer> getQualityIndexDao() {
		if (QualityIndexDao == null) {
			try {
				QualityIndexDao = getDao(QualityIndex.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return QualityIndexDao;
	}
	
	public Dao<SystenConfig, Integer> getSystenConfigDao() {
		if (SystenConfigDao == null) {
			try {
				SystenConfigDao = getDao(SystenConfig.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return SystenConfigDao;
	}
}
