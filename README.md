# start-activity-notification

Nội dung bài học

Giới thiệu

Start một activity thông thường

Start một activity đặc biệt

Ví dụ minh họa và bài tập



Giới thiệu

Khi ta start một activity từ một notification, ta phải đảm bảo trải nghiệm điều hướng mong muốn của người dùng.

Việc nhấn vào nút Back cần phải đưa người dùng trở lại từ luồng công việc thông thường ra màn hình chính và mở các màn hình gần đây nên hiển thị activity như một tác vụ độc lập.

Để đảm bảo trải nghiệm điều hướng này, ta nên start activity trong một task mới.

Có hai loại activity thường được khởi chạy từ một notification:

Các activity thông thường: đây là một activity tồn tại như một phần luồng UX thông thường của ứng dụng. Do đó khi người dùng đi tới activity từ notification, task mới nên bao gồm một backstack hoàn chỉnh và cho phép người dùng nhấn Back để điều hướng qua lại trong phân cấp ứng dụng.

Các activity đặc biệt: người dùng chỉ nhìn thấy activity loại này khi nó được khởi chạy từ notification. Nói cách khác, activity này kế thừa giao diện UI của notification bằng cách cung cấp thông tin khó có thể hiển thị hết trên notification. Do đó activity này không cần một backstack.

Start một activity thông thường

Để khởi chạy một activity từ notification, thiết lập PendingIntent sử dụng TaskStackBuilder để nó tạo một back stack mới.

Định nghĩa phân cấp Activity trong ứng dụng của ta

Định nghĩa một phân cấp tự nhiên cho các activity của ta bằng cách thêm thuộc tính android:parentActivityName cho từng activity khai báo trong file Manifest.

Ví dụ:

		  <activity
		      android:name=".MainActivity"
		      android:label="@string/app_name" >
		      <intent-filter>
		          <action android:name="android.intent.action.MAIN" />
		          <category android:name="android.intent.category.LAUNCHER" />
		      </intent-filter>
		  </activity>
		  <!-- MainActivity is the parent for ResultActivity -->
		  <activity
		      android:name=".ResultActivity"
		      android:parentActivityName=".MainActivity" />
		      ...
		  </activity>
  
Tạo một PendingIntent với một back stack

Để start một activity chứa một back stack của các activity đang hoạt động, ta cần tạo một phiên bản đối tượng của TaskStackBuilder và gọi addNextIntentWithParentStack(), truyền vào đó Intent chứa activity ta muốn khởi chạy.

Ngay sau khi ta định nghĩa activity cha cho từng activity, ta có thể gọi getPendingIntent() để nhận một PendingIntent chứa toàn bộ back stack:

// kotlin:

// tạo một Intent chứa activity ta muốn start

  		val resultIntent = Intent(this, ResultActivity::class.java)
  
// tạo TaskStackBuilder

		  val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
		      // thêm intent để sau đó nó thiết lập back stack
		      addNextIntentWithParentStack(resultIntent)
		      // lấy PendingIntent chứa toàn bộ back stack
		      getPendingIntent(0,
		              PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
		  }
		  
Nếu cần, ta có thể thêm các đối số cho đối tượng Intent trong stack bằng cách gọi tới TaskStackBuilder.editIntentAt().

Đôi khi ta cần làm vậy để đảm bảo rằng một activity trong back stack hiển thị dữ liệu có ý nghĩa khi người dùng điều hướng tới nó.

Sau đó ta có thể truyền PendingIntent vào một notification:

// kotlin:

		  val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
		      setContentIntent(resultPendingIntent)
		      ...
		  }
		  with(NotificationManagerCompat.from(this)) {
		      notify(NOTIFICATION_ID, builder.build())
		  }
  
Start một activity đặc biệt

Bởi vì một activity đặc biệt được khởi chạy từ notification không cần tới back stack nên ta có thể tạo PendingIntent bằng cách gọi getActivity(), nhưng ta vẫn cần định nghĩa tùy chọn task phù hợp trong file Manifest.

Trong file Manifest, thêm thuộc tính sau vào cho phần tử <activity>:

android:taskAffinity=”” kết hợp với cờ FLAG_ACTIVITY_NEW_TASK ta sẽ sử dụng trong code. Thiết lập giá trị này trống để đảm bảo activity này không đi vào task mặc định của ứng dụng. Bất kì task nào đang có trong ứng dụng đều không bị ảnh hưởng.

android:excludeFromRecents=”true” nhằm đảm bảo rằng người dùng không lỡ tay điều hướng quay trở lại activity này.

Ví dụ:

    <activity
        android:name=".ResultActivity"
        android:launchMode="singleTask"
        android:taskAffinity=""
        android:excludeFromRecents="true">
    </activity>
  
Build và gửi thông báo:

B1: tạo một Intent để start activity đích.

B2: thiết lập activity để start trong một task trống mới bằng cách gọi tới setFlags() với các cờ FLAG_ACTIVITY_NEW_TASK và FLAG_ACTIVITY_CLEAR_TASK.

B3: tạo một PendingIntent bằng cách gọi getActivity().

Ví dụ:

// kotlin:

      val notifyIntent = Intent(this, ResultActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      val notifyPendingIntent = PendingIntent.getActivity(
              this, 0, notifyIntent,
              PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
Cuối cùng, ta có thể truyền PendingIntent này vào notification như bình thường:

// kotlin:

  	val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
		      setContentIntent(notifyPendingIntent)
		      ...
		  }
		  with(NotificationManagerCompat.from(this)) {
		      notify(NOTIFICATION_ID, builder.build())
		  }
Ví dụ minh họa và bài tập


