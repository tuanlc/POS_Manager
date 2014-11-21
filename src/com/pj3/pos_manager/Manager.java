package com.pj3.pos_manager;


//general dependencies
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//local dependencies
import com.pj3.pos_manager.MainActivity;
import com.pj3.pos_manager.R;
import com.pj3.pos_manager.database.DatabaseSource;
import com.pj3.pos_manager.res_obj.Employee;
import com.pj3.pos_manager.res_obj.Position;

//android dependencies
import android.media.Image;
import android.net.Uri;

import com.pj3.pos_manager.R;
import com.pj3.pos_manager.res_obj.Employee;

import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
//restlet dependencies
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;
import org.restlet.Component;

public class Manager extends Activity{
	TabHost tabHost;
	List<Employee> employees;
	Spinner spPosition;
	GridLayout gridEmployees;
	PopupWindow popupWindow;
	Map<LinearLayout, Employee> itemEmployee;
	public static DatabaseSource db ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		db = new DatabaseSource(this);
		main_employee();
	}
	
	//Phan nay cua Le Cong Tuan------------------------------------------------------
	public void main_employee(){
		employees = new ArrayList<Employee>();
		itemEmployee = new HashMap<LinearLayout, Employee>();
		gridEmployees = (GridLayout) findViewById(R.id.gridEmployee);
		spPosition = (Spinner) findViewById(R.id.spPosition);
		addEmployee();
		loadTab();
		loadSpPosition();
		loadGridEmployees(loadEmploy());
		handlerSpinerPos();
	}
	
	public void initTable_Position(){
		Position position = new Position();
		position.setP_id(1);
		position.setP_name("Quản lí");
		position.setP_salary(5000000);

		db.createPosition(position);
		position.setP_id(2);
		position.setP_name("Bồi bàn");
		position.setP_salary(3000000);

		db.createPosition(position);
		position.setP_id(3);
		position.setP_name("Đầu bếp");
		position.setP_salary(3500000);

		db.createPosition(position);
	}
	
	public List<Employee> loadEmploy(){
		List<Employee> employeess = new ArrayList<Employee>();
		int select = spPosition.getSelectedItemPosition();
		if(select == 0){
			employeess = db.getAllUsers();
		}else if(select == 1){
			employeess = db.getUserByPosition(1);
		}else if(select == 2){
			employeess = db.getUserByPosition(2);
		}else if(select == 3){
			employeess = db.getUserByPosition(3);
		}
		return employeess;
	}
	
	public void loadSpPosition(){
		List<Position> positions = db.getPositions();
		List<String> sList = new ArrayList<String>();
		sList.add("Tất cả");
		int size = positions.size();
		for(int i = 0; i < size; i++){
			sList.add(positions.get(i).getP_name());
		}
		
		ArrayAdapter<String> adapterPostion = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sList);
		
		spPosition.setAdapter(adapterPostion);
		
	}
	
	@SuppressLint("NewApi")
	public void handlerSpinerPos(){
		spPosition.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				resetGridview();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	public void addEmployee(){
		ImageView addPicture = (ImageView) findViewById(R.id.addUserAction);
		addPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				active_Add_Employee();
			}
		});
	}
	
	public void active_Add_Employee(){
		final Dialog dialogAdd = new Dialog(Manager.this);
    	dialogAdd.setContentView(R.layout.e_info_action);
    	dialogAdd.setTitle("Thêm nhân viên");
    	Button cancelButton = (Button)dialogAdd.findViewById(R.id.m_e_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogAdd.dismiss();
			}
		});
    	Button doneButton = (Button)dialogAdd.findViewById(R.id.m_e_done_button);
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText e_name = (EditText) dialogAdd.findViewById(R.id.e_name_tb);
				RadioGroup radio_position = (RadioGroup) dialogAdd.findViewById(R.id.radio_position);
				RadioButton r_manager = (RadioButton) dialogAdd.findViewById(R.id.radio_manager);
        		RadioButton r_waiter = (RadioButton) dialogAdd.findViewById(R.id.radio_waiter);
        		RadioButton r_chef = (RadioButton) dialogAdd.findViewById(R.id.radio_chef);
				int position = 0;
				if(r_manager.isChecked()){
					position = 1;
				}else if(r_waiter.isChecked()){
					position = 2;
				}else if(r_chef.isChecked()){
					position = 3;
				}
				
				EditText e_email = (EditText) dialogAdd.findViewById(R.id.e_email_tb);
				EditText e_phone = (EditText) dialogAdd.findViewById(R.id.e_phoneNumber_tb);
				EditText e_pass = (EditText) dialogAdd.findViewById(R.id.e_pass_tb);
				String name = e_name.getText().toString();
				String email = e_email.getText().toString();
				String pass = e_pass.getText().toString();
				String phone = e_phone.getText().toString();
				if (!checkNull(name) && !checkNull(email)
						&& !checkNull(pass) && position != 0
						&& !checkNull(phone) && email.contains("@")) {
					try{
						Employee e = new Employee(name, email, pass, "Anh",
								Integer.valueOf(phone), position);
						db.createUser(e);
						resetGridview();
						dialogAdd.setTitle("Thêm nhân viên");
						Toast.makeText(getApplicationContext(),
								"Thêm nhân viên thành công!",
								Toast.LENGTH_SHORT).show();
						dialogAdd.dismiss();
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),
								"Số điện thoại phải là số!",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					dialogAdd.setTitle("Thêm nhân viên. Chưa điền đầy đủ thông tin");
					Toast.makeText(getApplicationContext(),
							"Hãy chắc chắn đã điền đầy đủ thông tin!",
							Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		
		ImageView profile = (ImageView)dialogAdd.findViewById(R.id.picture_profile);
		profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Chưa xử lí chọn ảnh", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	dialogAdd.show();
    	
	}
	
	public boolean checkNull(String input){
		return input.equals(" ");
	}
	
	@SuppressLint("NewApi")
	public void resetGridview(){
		gridEmployees.removeAllViews();
		loadGridEmployees(loadEmploy());
	}
	@SuppressLint("NewApi")
	public void loadGridEmployees(final List<Employee> employees){
		int numberEmployee_current = employees.size();
		for(int i = 0; i < numberEmployee_current; i++){
			LinearLayout item = new LinearLayout(Manager.this);
			item.setOrientation(LinearLayout.VERTICAL);
			item.setPadding(50, 50, 50, 50);
			
			item.setWeightSum(3);
			final int k = i;
			itemEmployee.put(item, employees.get(i));
			item.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					final Employee e_item = employees.get(k);
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(Manager.this);
					alertDialog.setIcon(R.drawable.ic_launcher);
	                alertDialog.setTitle("Quản lí nhân viên: "+ e_item.getE_name());
	 
	                alertDialog.setPositiveButton("Xem", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                    	viewEmployee(e_item);
	                    }
	                });
	 
	                // Setting Negative "Edit" Button
	                alertDialog.setNegativeButton("Chỉnh sửa", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                    	editEmployee(e_item);
	                    }
	                });
	 
	                // Setting Netural "delete" Button
	                alertDialog.setNeutralButton("Xóa", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                    	AlertDialog dialogConfirm = new AlertDialog.Builder(Manager.this)
	                    	.setMessage("Bạn chắc chắn muốn xóa " + e_item.getE_name() + " ra khỏi danh sách?")
	                    	.setIcon(R.drawable.ic_launcher)
	                    	.setTitle("Xác nhận")
	                    	.setPositiveButton("Có", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									boolean ok = db.deleteUser(e_item.getE_id());
				                    if(ok){
				                    	resetGridview();
				                    	Toast.makeText(getApplicationContext(), "Xóa thành công!",
				                                        Toast.LENGTH_SHORT).show();
				                    	}else Toast.makeText(getApplicationContext(), "Xóa thất bại!",
		                                        Toast.LENGTH_SHORT).show();
								}
							})
	                    	.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							})
	                    	.create();
	                    	dialogConfirm.show();
		                    }
	                });
	 
	                // Showing Alert Message
	                alertDialog.show();
					return false;
				}
			});
			
			ImageView image = new ImageView(Manager.this);
			image.setBackgroundResource(R.drawable.man_brown);
			
			TextView name = new TextView(Manager.this);
			name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			name.setText(employees.get(i).getE_name());
			
			TextView phoneNumber = new TextView(Manager.this);
			phoneNumber.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			phoneNumber.setText(""+employees.get(i).getE_phone_number());
			
			item.addView(image);
			item.addView(name);
			item.addView(phoneNumber);
			
			gridEmployees.addView(item);
		}
		
	}
	public void viewEmployee(final Employee e_item){

    	final Dialog dialogView = new Dialog(Manager.this);
		dialogView.setContentView(R.layout.e_info_action);
		dialogView.setTitle("Xem nhân viên");
		
		EditText e_name = (EditText) dialogView.findViewById(R.id.e_name_tb);
		e_name.setText(e_item.getE_name());
		e_name.setFocusable(false);
		
		RadioGroup radio_position = (RadioGroup) dialogView.findViewById(R.id.radio_position);
		RadioButton r_manager = (RadioButton) dialogView.findViewById(R.id.radio_manager);
		RadioButton r_waiter = (RadioButton) dialogView.findViewById(R.id.radio_waiter);
		RadioButton r_chef = (RadioButton) dialogView.findViewById(R.id.radio_chef);
		if(e_item.getPOSITION_p_id() == 1){
			radio_position.check(r_manager.getId());
		}else if(e_item.getPOSITION_p_id() == 2){
			radio_position.check(r_waiter.getId());
		}else if(e_item.getPOSITION_p_id() == 3){
			radio_position.check(r_chef.getId());
		}
		
		r_manager.setEnabled(false);
		r_waiter.setEnabled(false);
		r_chef.setEnabled(false);
		
		EditText e_email = (EditText) dialogView.findViewById(R.id.e_email_tb);
		e_email.setText(e_item.getE_email());
		e_email.setFocusable(false);
		
		EditText e_phone = (EditText) dialogView.findViewById(R.id.e_phoneNumber_tb);
		e_phone.setText(""+e_item.getE_phone_number());
		e_phone.setFocusable(false);
		
		EditText e_pass = (EditText) dialogView.findViewById(R.id.e_pass_tb);
		e_pass.setText(""+e_item.getE_pass());
		e_pass.setFocusable(false);
		
		Button cancelButton = (Button)dialogView.findViewById(R.id.m_e_cancel_button);
		cancelButton.setText("Xong");
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogView.dismiss();
			}
		});
		Button doneButton = (Button)dialogView.findViewById(R.id.m_e_done_button);
		doneButton.setText("Chỉnh sửa");
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogView.dismiss();
				editEmployee(e_item);
			}
		});
		
		ImageView profile = (ImageView)dialogView.findViewById(R.id.picture_profile);
		profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Chưa xử lí chọn ảnh", Toast.LENGTH_SHORT).show();
			}
		});
		dialogView.show();
	}
	
	public void editEmployee(final Employee e_item){

    	final Dialog dialogEdit = new Dialog(Manager.this);
    	dialogEdit.setContentView(R.layout.e_info_action);
    	dialogEdit.setTitle("Chỉnh sửa");
		
		final EditText e_name = (EditText) dialogEdit.findViewById(R.id.e_name_tb);
		e_name.setText(e_item.getE_name());
		
		final RadioGroup radio_position = (RadioGroup) dialogEdit.findViewById(R.id.radio_position);
		final RadioButton r_manager = (RadioButton) dialogEdit.findViewById(R.id.radio_manager);
		final RadioButton r_waiter = (RadioButton) dialogEdit.findViewById(R.id.radio_waiter);
		final RadioButton r_chef = (RadioButton) dialogEdit.findViewById(R.id.radio_chef);
		if(e_item.getPOSITION_p_id() == 1){
			radio_position.check(r_manager.getId());
		}else if(e_item.getPOSITION_p_id() == 2){
			radio_position.check(r_waiter.getId());
		}else if(e_item.getPOSITION_p_id() == 3){
			radio_position.check(r_chef.getId());
		}
		
		final EditText e_email = (EditText) dialogEdit.findViewById(R.id.e_email_tb);
		e_email.setText(e_item.getE_email());
		
		final EditText e_phone = (EditText) dialogEdit.findViewById(R.id.e_phoneNumber_tb);
		e_phone.setText(""+e_item.getE_phone_number());
		
		final EditText e_pass = (EditText) dialogEdit.findViewById(R.id.e_pass_tb);
		e_pass.setText(""+e_item.getE_pass());
		
		Button cancelButton = (Button)dialogEdit.findViewById(R.id.m_e_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogEdit.dismiss();
			}
		});
		Button doneButton = (Button)dialogEdit.findViewById(R.id.m_e_done_button);
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = 1;
				if(r_manager.isChecked()){
					position = 1;
				}else if(r_waiter.isChecked()){
					position = 2;
				}else if(r_chef.isChecked()){
					position = 3;
				}
				String name = e_name.getText().toString();
				String email = e_email.getText().toString();
				String pass = e_pass.getText().toString();
				String phone = e_phone.getText().toString();
				if (!checkNull(name) && !checkNull(email)
						&& !checkNull(pass) && position != 0
						&& !checkNull(phone) && email.contains("@")) {
					e_item.setE_name(name);
					e_item.setPOSITION_p_id(position);
					e_item.setE_email(email);
					
					e_item.setE_pass(pass);
					try{
						e_item.setE_phone_number(Integer.valueOf(phone));
						boolean ok = db.updateUser(e_item);
						if(ok){
							dialogEdit.setTitle("Chỉnh sửa");
							resetGridview();
							Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
							dialogEdit.dismiss();
						}
						resetGridview();
					}catch(Exception e){
						dialogEdit.setTitle("Chỉnh sửa. Số điện thoại phải là số!");
					}

					
				} else {
					dialogEdit.setTitle("Chỉnh sửa. Chưa điền đầy đủ thông tin hoặc Email sai!");
					Toast.makeText(getApplicationContext(),
							"Hãy chắc chắn đã điền đầy đủ thông tin!",
							Toast.LENGTH_SHORT).show();
				}
				
				
				
				
			}
		});
		
		final ImageView profile = (ImageView)dialogEdit.findViewById(R.id.picture_profile);
		profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chooseImage();
				Toast.makeText(getApplicationContext(), "Chưa xử lí chọn ảnh", Toast.LENGTH_SHORT).show();
			}
		});
		dialogEdit.show();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.manager_employee_context_button, menu);
	}
	
	


	public void loadTab(){
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		TabSpec spec;
		
		spec = tabHost.newTabSpec("tab1");
		spec.setIndicator("Thanh Toán");
		spec.setContent(R.id.payment);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab2");
		spec.setIndicator("Menu");
		spec.setContent(R.id.menu);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab3");
		spec.setIndicator("Nhân viên");
		spec.setContent(R.id.employee);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab4");
		spec.setIndicator("Thống kê");
		spec.setContent(R.id.statistic);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}
	
	private static final int PICK_IMAGE = 1;
	private static String url_image = "";
	public void chooseImage(){
		
		Intent intent_image= new Intent();
		intent_image.setType("image/*");
		intent_image.setAction(Intent.ACTION_GET_CONTENT);
		Bundle bundle = new Bundle();
		startActivityForResult(Intent.createChooser(intent_image, "Select picture"), PICK_IMAGE);
		
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            url_image = picturePath;
			}
	        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager, menu);
		return true;
	}
	//-------------------------------------------------------------------------------------------

}
